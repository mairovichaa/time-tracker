package time_tracker.service;

import time_tracker.annotation.NonNull;
import time_tracker.domain.DayStatistics;

import java.time.LocalDate;
import java.util.List;

public interface DayStatisticsService {

    @NonNull
    List<DayStatistics> findAll();

    @NonNull
    DayStatistics create(@NonNull LocalDate date);

    void save(@NonNull DayStatistics dayStatistics);
}
