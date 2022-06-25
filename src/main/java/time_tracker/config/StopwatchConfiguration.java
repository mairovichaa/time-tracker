package time_tracker.config;

import time_tracker.annotation.NonNull;
import time_tracker.component.stopwatch.StopWatchTab;
import time_tracker.repository.StopwatchRecordRepository;
import time_tracker.service.DefaultStopwatchRecordService;
import time_tracker.service.StopwatchRecordService;

public class StopwatchConfiguration {

    public StopwatchRecordService stopwatchRecordService(@NonNull final StopwatchRecordRepository stopwatchRecordRepository) {
        var defaultStopwatchRecordService = new DefaultStopwatchRecordService(stopwatchRecordRepository);
        defaultStopwatchRecordService.create("Всякое");
        return defaultStopwatchRecordService;
    }

    public StopwatchRecordRepository stopwatchRecordRepository() {
        return new StopwatchRecordRepository();
    }

    public StopWatchTab stopWatchTab(
            @NonNull final StopwatchRecordService stopwatchRecordService
    ) {
        System.out.println("Creating stopwatch tab");
        return new StopWatchTab(stopwatchRecordService);
    }

}
