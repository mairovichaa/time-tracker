package time_tracker.config;

import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.component.stopwatch.StopWatchTab;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopWatchAppState;
import time_tracker.repository.StopwatchRecordFileRepository;
import time_tracker.repository.StopwatchRecordRepository;
import time_tracker.service.*;
import time_tracker.service.dev.RandomStopwatchRecordFactory;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.logging.Level;

@Log
public class StopwatchConfiguration {

    @NonNull
    public StopwatchRecordService stopwatchRecordService(
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final StopwatchRecordRepository stopwatchRecordRepository,
            @NonNull final StopwatchRecordOnLoadFactory stopwatchRecordOnLoadFactory
    ) {
        log.log(Level.FINE, "Creating stopwatchRecordService");
        return GlobalContext.createStoreAndReturn(
                StopwatchRecordService.class,
                () -> new DefaultStopwatchRecordService(stopWatchAppState, stopwatchRecordRepository, stopwatchRecordOnLoadFactory)
        );
    }

    @NonNull
    public StopwatchRecordOnLoadFactory stopwatchRecordOnLoadFactory(@NonNull final StopwatchProperties stopwatchProperties) {
        log.log(Level.FINE, "Creating stopwatchRecordOnLoadFactory");
        return GlobalContext.createStoreAndReturn(
                StopwatchRecordOnLoadFactory.class,
                () -> new StopwatchRecordOnLoadFactoryImpl(stopwatchProperties)
        );
    }

    @NonNull
    public StopWatchAppState stopWatchAppState() {
        log.log(Level.FINE, "Creating stopWatchAppState");
        var stopWatchAppState = new StopWatchAppState();
        var today = LocalDate.now();
        stopWatchAppState.setChosenDate(today);
        return GlobalContext.createStoreAndReturn(
                StopWatchAppState.class,
                () -> stopWatchAppState
        );
    }

    @NonNull
    public StopwatchRecordRepository stopwatchRecordRepository(
            @NonNull final StopwatchProperties stopwatchProperties
    ) {
        log.log(Level.FINE, "Creating stopwatchRecordRepository");
        var folderWithData = stopwatchProperties.getFolderWithData();
        var path = Paths.get(folderWithData);
        return GlobalContext.createStoreAndReturn(
                StopwatchRecordRepository.class,
                () -> new StopwatchRecordFileRepository(path)
        );
    }

    @NonNull
    public StopWatchTab stopWatchTab() {
        log.log(Level.FINE, "Creating stopWatchTab");
        return GlobalContext.createStoreAndReturn(
                StopWatchTab.class,
                StopWatchTab::new
        );
    }

    @NonNull
    public StopwatchRecordSearchService stopwatchRecordSearchService(
            @NonNull final StopwatchRecordRepository stopwatchRecordRepository,
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final StopwatchProperties stopwatchProperties
    ) {
        log.log(Level.FINE, "Creating stopwatchRecordSearchService");
        return GlobalContext.createStoreAndReturn(
                StopwatchRecordSearchService.class,
                () -> new StopwatchRecordSearchServiceImpl(
                        stopwatchRecordRepository,
                        stopWatchAppState.getChosenDate(),
                        stopwatchProperties
                )
        );
    }

    @NonNull
    public RandomStopwatchRecordFactory randomStopwatchRecordFactory(
            @NonNull final StopwatchRecordService stopwatchRecordService
    ) {
        log.log(Level.FINE, "Creating randomStopwatchRecordFactory");
        return GlobalContext.createStoreAndReturn(
                RandomStopwatchRecordFactory.class,
                () -> new RandomStopwatchRecordFactory(stopwatchRecordService)
        );
    }
}
