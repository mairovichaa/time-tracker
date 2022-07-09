package time_tracker.config;

import time_tracker.annotation.NonNull;
import time_tracker.component.stopwatch.StopWatchTab;
import time_tracker.model.StopWatchAppState;
import time_tracker.repository.StopwatchRecordConsoleRepository;
import time_tracker.repository.StopwatchRecordFileRepository;
import time_tracker.repository.StopwatchRecordRepository;
import time_tracker.service.DefaultStopwatchRecordService;
import time_tracker.service.StopwatchRecordService;
import time_tracker.service.dev.RandomStopwatchRecordFactory;

import java.nio.file.Paths;
import java.time.LocalDate;

public class StopwatchConfiguration {

    //    TODO move to enum???
    private static final String CONSOLE_REPOSITORY_TYPE = "console";
    private static final String FILE_REPOSITORY_TYPE = "file";
    // TODO move to configuration file
    private final String typeOfRepository = FILE_REPOSITORY_TYPE;

    private final String pathToFolderWithData = "/home/andrey/Documents/time-tracker-dev";

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

    public StopwatchRecordRepository stopwatchRecordRepository() {
        if (typeOfRepository.equals(FILE_REPOSITORY_TYPE)) {
            var path = Paths.get(pathToFolderWithData);
            return new StopwatchRecordFileRepository(path);
        }
        if (typeOfRepository.equals(CONSOLE_REPOSITORY_TYPE)) {
            return new StopwatchRecordConsoleRepository();
        }
        throw new IllegalArgumentException("Unknown type of repository: " + typeOfRepository);
    }

    public StopWatchTab stopWatchTab(
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final StopwatchRecordService stopwatchRecordService,
            @NonNull final StopwatchRecordRepository stopwatchRecordRepository,
            @NonNull final RandomStopwatchRecordFactory randomStopwatchRecordFactory
    ) {
        System.out.println("Creating stopwatch tab");
        return new StopWatchTab(stopWatchAppState, stopwatchRecordService, stopwatchRecordRepository, randomStopwatchRecordFactory);
    }

}
