package time_tracker.service;

import time_tracker.annotation.NonNull;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;

import java.util.List;

public interface StopwatchRecordService {

    StopwatchRecord create(@NonNull String name);

    StopwatchRecordMeasurement startNewMeasurement(@NonNull StopwatchRecord record);

    void stopMeasurement(@NonNull StopwatchRecord stopwatchRecord);

    void store();
}
