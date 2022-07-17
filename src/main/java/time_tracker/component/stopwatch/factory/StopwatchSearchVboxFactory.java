package time_tracker.component.stopwatch.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.component.stopwatch.StopwatchSearchVbox;
import time_tracker.config.StopwatchSearchState;

@Log
@RequiredArgsConstructor
public class StopwatchSearchVboxFactory {

    @NonNull
    private final StopwatchSearchState stopwatchSearchState;

    @NonNull
    public StopwatchSearchVbox create() {
        log.fine("create");
        return new StopwatchSearchVbox(stopwatchSearchState);
    }

}
