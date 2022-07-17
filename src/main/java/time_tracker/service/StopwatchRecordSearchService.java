package time_tracker.service;

import time_tracker.annotation.NonNull;
import time_tracker.config.StopwatchSearchState;

public interface StopwatchRecordSearchService {

    void initialize(@NonNull final StopwatchSearchState stopwatchSearchState);

}
