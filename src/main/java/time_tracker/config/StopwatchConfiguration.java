package time_tracker.config;

import time_tracker.annotation.NonNull;
import time_tracker.component.stopwatch.StopWatchTab;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopWatchAppState;
import time_tracker.repository.StopwatchRecordFileRepository;
import time_tracker.repository.StopwatchRecordRepository;
import time_tracker.service.DefaultStopwatchRecordService;
import time_tracker.service.StopwatchRecordService;
import time_tracker.service.dev.RandomStopwatchRecordFactory;

import java.nio.file.Paths;
import java.time.LocalDate;

public class StopwatchConfiguration {

    public StopwatchRecordService stopwatchRecordService(
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final StopwatchRecordRepository stopwatchRecordRepository
    ) {
        var defaultStopwatchRecordService = new DefaultStopwatchRecordService(stopWatchAppState, stopwatchRecordRepository);
        defaultStopwatchRecordService.create("Всякое");
        return defaultStopwatchRecordService;
    }

    public StopWatchAppState stopWatchAppState() {
        var stopWatchAppState = new StopWatchAppState();
        var today = LocalDate.now();
        stopWatchAppState.setChosenDate(today);
        return stopWatchAppState;
    }

    public StopwatchRecordRepository stopwatchRecordRepository(
            @NonNull final StopwatchProperties stopwatchProperties
    ) {
        var folderWithData = stopwatchProperties.getFolderWithData();
        var path = Paths.get(folderWithData);
        return new StopwatchRecordFileRepository(path);
    }

    public StopWatchTab stopWatchTab(
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final StopwatchRecordService stopwatchRecordService,
            @NonNull final StopwatchRecordRepository stopwatchRecordRepository,
            @NonNull final RandomStopwatchRecordFactory randomStopwatchRecordFactory,
            @NonNull final StopwatchProperties stopwatchProperties
    ) {
        System.out.println("Creating stopwatch tab");
        return new StopWatchTab(stopWatchAppState, stopwatchRecordService, stopwatchRecordRepository, randomStopwatchRecordFactory, stopwatchProperties);
    }

}
