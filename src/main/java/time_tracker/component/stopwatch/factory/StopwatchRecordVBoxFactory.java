package time_tracker.component.stopwatch.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.component.stopwatch.StopwatchRecordVBox;
import time_tracker.model.StopwatchRecord;
import time_tracker.service.StopwatchRecordService;

@Log
@RequiredArgsConstructor
public class StopwatchRecordVBoxFactory {

    @NonNull
    private final StopwatchRecordService stopwatchRecordService;

    @NonNull
    public StopwatchRecordVBox create(@NonNull final StopwatchRecord record) {
        log.fine("create");
        return new StopwatchRecordVBox(record, stopwatchRecordService);
    }
}
