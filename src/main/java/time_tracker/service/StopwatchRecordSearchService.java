package time_tracker.service;

import time_tracker.annotation.NonNull;
import time_tracker.config.StopwatchSearchState;
import time_tracker.model.StopWatchAppState;

public interface StopwatchRecordSearchService {

    void initialize(@NonNull final StopwatchSearchState stopwatchSearchState, @NonNull final StopWatchAppState stopWatchAppState);

}
