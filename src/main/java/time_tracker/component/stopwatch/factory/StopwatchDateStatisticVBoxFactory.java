package time_tracker.component.stopwatch.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.component.stopwatch.StopwatchDateStatisticVBox;
import time_tracker.service.StopwatchRecordService;

@RequiredArgsConstructor
@Log
public class StopwatchDateStatisticVBoxFactory {
    @NonNull
    private final StopwatchRecordService stopwatchRecordService;

    @NonNull
    public StopwatchDateStatisticVBox create() {
        log.fine("create");
        return new StopwatchDateStatisticVBox(stopwatchRecordService);
    }
}
