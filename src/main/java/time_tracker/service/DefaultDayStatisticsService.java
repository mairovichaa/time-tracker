package time_tracker.service;

import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.domain.DayStatistics;
import time_tracker.repository.DayStatisticsRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;

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
    @NonNull
    public DayStatistics create(LocalDate date) {
        log.finest(() -> "create()");
        for (DayStatistics it : data) {
            if (it.getDate().equals(date)) {
                var msg = "day statistics for " + date + " has been already created";
                log.log(Level.WARNING, () -> msg);
                throw new IllegalArgumentException(msg);
            }
        }

        var dayStatistics = new DayStatistics();
        dayStatistics.setDate(date);

        data.add(dayStatistics);
        dayStatisticsRepository.save(data);

        return dayStatistics;
    }

    @Override
    public void save(DayStatistics dayStatistics) {
        log.finest(() -> "save()");

        data.removeIf(it -> it.getDate().equals(dayStatistics.getDate()));
        data.add(dayStatistics);

        dayStatisticsRepository.save(data);
    }
}
