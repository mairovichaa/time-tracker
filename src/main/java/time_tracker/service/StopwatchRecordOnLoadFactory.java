package time_tracker.service;

import time_tracker.annotation.NonNull;
import time_tracker.model.StopwatchRecord;

import java.time.LocalDate;
import java.util.List;

public interface StopwatchRecordOnLoadFactory {
    @NonNull
    List<StopwatchRecord> create(@NonNull LocalDate date);
}
