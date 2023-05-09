package time_tracker.service;

import time_tracker.annotation.NonNull;
import time_tracker.model.StopwatchSearchState;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;

import java.util.List;

public interface StopwatchRecordSearchService {

    void initialize(
            @NonNull final StopwatchSearchState stopwatchSearchState,
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final StopwatchRecordSearchService stopwatchRecordSearchService);

    void shutdown();

    @NonNull
    List<StopwatchRecord> recordsByName(@NonNull String name);
}
