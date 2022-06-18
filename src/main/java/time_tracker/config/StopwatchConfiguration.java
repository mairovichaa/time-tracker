package time_tracker.config;

import lombok.NonNull;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import time_tracker.component.stopwatch.StopWatchTab;
import time_tracker.service.DefaultStopwatchRecordService;
import time_tracker.service.StopwatchRecordService;

public class StopwatchConfiguration {

    public StopwatchRecordService stopwatchRecordService() {
        return new DefaultStopwatchRecordService();
    }

    public StopWatchTab stopWatchTab(
            @NonNull final StopwatchRecordService stopwatchRecordService
    ) {
        System.out.println("Creating stopwatch tab");
        return new StopWatchTab(stopwatchRecordService);
    }

}
