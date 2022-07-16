package time_tracker.service;

import time_tracker.model.StopwatchRecord;

import java.util.List;

public interface StopwatchRecordOnLoadFactory {
    List<StopwatchRecord> create();
}
