package time_tracker.repository;

import time_tracker.common.annotation.NonNull;
import time_tracker.domain.DayStatistics;

import java.util.List;

public interface DayStatisticsRepository {

    @NonNull
    List<DayStatistics> findAll();
    void save(@NonNull final List<DayStatistics> data);

    @NonNull
    Long nextIdForDayStatistics();


}
