package time_tracker.service;

import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.domain.DayStatistics;
import time_tracker.model.StopWatchAppState;
import time_tracker.repository.DayStatisticsRepository;

import java.util.List;

@Log
public class DefaultDayStatisticsService implements DayStatisticsService {

    private final DayStatisticsRepository dayStatisticsRepository;

    private final List<DayStatistics> data;

    public DefaultDayStatisticsService(
            @NonNull final DayStatisticsRepository dayStatisticsRepository
    ) {
        this.dayStatisticsRepository = dayStatisticsRepository;
        data = dayStatisticsRepository.findAll();
    }

    @Override
    @NonNull
    public List<DayStatistics> findAll() {
        log.finest(() -> "findAll()");
        return data;
    }

    @Override
    public void save(DayStatistics dayStatistics) {
        log.finest(() -> "save()");

        data.removeIf(it -> it.getDate().equals(dayStatistics.getDate()));
        data.add(dayStatistics);

        dayStatisticsRepository.save(data);
    }
}
