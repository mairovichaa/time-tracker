package time_tracker.service;

import time_tracker.common.annotation.NonNull;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StopwatchRecordService {

    StopwatchRecord create(@NonNull String name);

    StopwatchRecordMeasurement startNewMeasurement(@NonNull StopwatchRecord record);

    void stopMeasurement(@NonNull StopwatchRecord stopwatchRecord);

    void store();
    void store(@NonNull LocalDate date);
    void store(@NonNull StopwatchRecord record);

    void delete(@NonNull StopwatchRecord stopwatchRecord);

    void moveToDate(@NonNull StopwatchRecord record, @NonNull LocalDate newDate);

    Map<LocalDate, List<StopwatchRecord>> getLoaded();
}
