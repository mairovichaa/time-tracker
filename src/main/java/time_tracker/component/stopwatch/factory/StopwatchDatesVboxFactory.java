package time_tracker.component.stopwatch.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.component.stopwatch.StopwatchDatesVbox;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopWatchAppState;
import time_tracker.repository.StopwatchRecordRepository;
import time_tracker.service.StopwatchRecordService;

@Log
@RequiredArgsConstructor
public class StopwatchDatesVboxFactory {
    @NonNull
    private final StopWatchAppState stopWatchAppState;
    @NonNull
    private final StopwatchRecordService stopwatchRecordService;
    @NonNull
    private final StopwatchRecordRepository stopwatchRecordRepository;
    @NonNull
    private final StopwatchProperties.StopwatchDatesProperties stopwatchDatesProperties;

    @NonNull
    public StopwatchDatesVbox create() {
        log.fine("create");
        return new StopwatchDatesVbox(stopWatchAppState, stopwatchRecordService, stopwatchRecordRepository, stopwatchDatesProperties);
    }
}
