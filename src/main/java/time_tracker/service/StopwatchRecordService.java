package time_tracker.service;

import javafx.collections.ObservableList;
import lombok.NonNull;
import time_tracker.model.StopwatchRecord;

public interface StopwatchRecordService {
    ObservableList<StopwatchRecord> findAll();

    StopwatchRecord create(@NonNull String name);
}
