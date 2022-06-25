package time_tracker.config;

import lombok.NonNull;
import time_tracker.component.stopwatch.StopWatchTab;
import time_tracker.service.DefaultStopwatchRecordService;
import time_tracker.service.StopwatchRecordService;

public class StopwatchConfiguration {

    public StopwatchRecordService stopwatchRecordService() {
        var defaultStopwatchRecordService = new DefaultStopwatchRecordService();
        defaultStopwatchRecordService.create("Всякое");
        return defaultStopwatchRecordService;
    }

    public StopWatchTab stopWatchTab(
            @NonNull final StopwatchRecordService stopwatchRecordService
    ) {
        System.out.println("Creating stopwatch tab");
        return new StopWatchTab(stopwatchRecordService);
    }

}
