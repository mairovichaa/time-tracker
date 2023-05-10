package time_tracker.repository;


import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import time_tracker.common.annotation.NonNull;
import time_tracker.domain.DayStatistics;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
public class DefaultDayStatisticsRepository implements DayStatisticsRepository {
    private final static String FILENAME = "day-statistics";

    private final AtomicLong nextDayStatisticsId = new AtomicLong();
    private final FileRepository fileRepository;

    @Override
    @NonNull
    public List<DayStatistics> findAll() {
        var typeReference = new TypeReference<List<DayStatistics>>() {
        };
        var dayStatistics = fileRepository.get(typeReference, FILENAME);

        var nextDayStatisticsIdLong = dayStatistics.stream()
                .mapToLong(DayStatistics::getId)
                .max().orElse(0) + 1;
        nextDayStatisticsId.set(nextDayStatisticsIdLong);

        return dayStatistics;
    }

    @Override
    public void save(List<DayStatistics> data) {
        data.forEach(it -> {
            if (it.getId() == null) {
                it.setId(nextIdForDayStatistics());
            }
        });

        fileRepository.save(FILENAME, data);
    }

    @Override
    @NonNull
    public Long nextIdForDayStatistics() {
        return nextDayStatisticsId.getAndIncrement();
    }
}
