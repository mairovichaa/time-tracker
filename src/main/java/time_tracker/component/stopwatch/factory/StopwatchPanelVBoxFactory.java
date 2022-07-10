package time_tracker.component.stopwatch.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.component.stopwatch.StopwatchPanelVBox;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.service.StopwatchRecordService;
import time_tracker.service.dev.RandomStopwatchRecordFactory;

@Log
@RequiredArgsConstructor
public class StopwatchPanelVBoxFactory {
    @NonNull
    private final StopwatchRecordService stopwatchRecordService;
    @NonNull
    private final RandomStopwatchRecordFactory randomStopwatchRecordFactory;
    @NonNull
    private final StopwatchProperties stopwatchProperties;
    @NonNull
    private final StopwatchRecordVBoxFactory stopwatchRecordVBoxFactory;

    @NonNull
    public StopwatchPanelVBox create() {
        log.fine("create");
        return new StopwatchPanelVBox(stopwatchRecordService, stopwatchRecordVBoxFactory, randomStopwatchRecordFactory, stopwatchProperties);
    }

}
