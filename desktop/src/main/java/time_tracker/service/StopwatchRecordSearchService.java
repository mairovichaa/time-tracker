package time_tracker.service;

import time_tracker.common.annotation.NonNull;
import time_tracker.model.StopwatchSearchState;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;

import java.util.List;

public interface StopwatchRecordSearchService {

    void initialize();

    void shutdown();

    @NonNull
    List<StopwatchRecord> recordsByName(@NonNull String name);
}
