package time_tracker.service;

import javafx.collections.ObservableList;
import time_tracker.annotation.NonNull;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;

public interface StopwatchRecordService {
    ObservableList<StopwatchRecord> findAll();

    StopwatchRecord create(@NonNull String name);

    StopwatchRecordMeasurement startNewMeasurement(@NonNull StopwatchRecord record);

    void stopMeasurement(@NonNull StopwatchRecord stopwatchRecord);

    void store();
}
