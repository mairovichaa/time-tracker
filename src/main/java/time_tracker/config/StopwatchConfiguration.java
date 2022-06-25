package time_tracker.config;

import time_tracker.annotation.NonNull;
import time_tracker.component.stopwatch.StopWatchTab;
import time_tracker.repository.StopwatchRecordConsoleRepository;
import time_tracker.repository.StopwatchRecordFileRepository;
import time_tracker.repository.StopwatchRecordRepository;
import time_tracker.service.DefaultStopwatchRecordService;
import time_tracker.service.StopwatchRecordService;

import java.nio.file.Paths;

public class StopwatchConfiguration {

    //    TODO move to enum???
    private static final String CONSOLE_REPOSITORY_TYPE = "console";
    private static final String FILE_REPOSITORY_TYPE = "file";
    // TODO move to configuration file
    private final String typeOfRepository = FILE_REPOSITORY_TYPE;

    private final String pathToFolderWithData = "/home/andrey/Documents/time-tracker-dev";

    public StopwatchRecordService stopwatchRecordService(@NonNull final StopwatchRecordRepository stopwatchRecordRepository) {
        var defaultStopwatchRecordService = new DefaultStopwatchRecordService(stopwatchRecordRepository);
        defaultStopwatchRecordService.create("Всякое");
        return defaultStopwatchRecordService;
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
            @NonNull final StopwatchRecordService stopwatchRecordService
    ) {
        System.out.println("Creating stopwatch tab");
        return new StopWatchTab(stopwatchRecordService);
    }

}
