package time_tracker.service;

import time_tracker.annotation.NonNull;
import time_tracker.domain.DayStatistics;

import java.util.List;

public interface DayStatisticsService {

    @NonNull
    List<DayStatistics> findAll();

    void save(@NonNull DayStatistics dayStatistics);
}
