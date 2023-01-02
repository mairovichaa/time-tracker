package time_tracker.service;

import time_tracker.annotation.NonNull;
import time_tracker.model.DayData;

import java.time.LocalDate;
import java.util.Map;

public interface DayStatisticsService {
    void save();

    void enrich(@NonNull Map<LocalDate, DayData> dateToDayData);
}
