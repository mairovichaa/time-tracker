package time_tracker.repository;

import time_tracker.annotation.NonNull;
import time_tracker.model.StopwatchRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StopwatchRecordRepository {
    void store(@NonNull final List<StopwatchRecord> records, @NonNull final LocalDate date);

    Map<LocalDate, List<StopwatchRecord>> getLoaded();

    Long nextIdForRecord();

    Long nextIdForMeasurement();

}
