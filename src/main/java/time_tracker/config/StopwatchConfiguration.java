package time_tracker.config;

import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.component.stopwatch.StopWatchTab;
import time_tracker.component.stopwatch.factory.StopwatchDateStatisticVBoxFactory;
import time_tracker.component.stopwatch.factory.StopwatchDatesVboxFactory;
import time_tracker.component.stopwatch.factory.StopwatchPanelVBoxFactory;
import time_tracker.component.stopwatch.factory.StopwatchRecordVBoxFactory;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopWatchAppState;
import time_tracker.repository.StopwatchRecordFileRepository;
import time_tracker.repository.StopwatchRecordRepository;
import time_tracker.service.DefaultStopwatchRecordService;
import time_tracker.service.StopwatchRecordOnLoadFactory;
import time_tracker.service.StopwatchRecordOnLoadFactoryImpl;
import time_tracker.service.StopwatchRecordService;
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
        var defaultStopwatchRecordService = new DefaultStopwatchRecordService(stopWatchAppState, stopwatchRecordRepository, stopwatchRecordOnLoadFactory);
        defaultStopwatchRecordService.create("Всякое");
        return defaultStopwatchRecordService;
    }

    @NonNull
    public StopwatchRecordOnLoadFactory stopwatchRecordOnLoadFactory(@NonNull final StopwatchProperties stopwatchProperties) {
        log.log(Level.FINE, "Creating stopwatchRecordOnLoadFactory");
        return new StopwatchRecordOnLoadFactoryImpl(stopwatchProperties);
    }

    @NonNull
    public StopWatchAppState stopWatchAppState() {
        log.log(Level.FINE, "Creating stopWatchAppState");
        var stopWatchAppState = new StopWatchAppState();
        var today = LocalDate.now();
        stopWatchAppState.setChosenDate(today);
        return stopWatchAppState;
    }

    @NonNull
    public StopwatchRecordRepository stopwatchRecordRepository(
            @NonNull final StopwatchProperties stopwatchProperties
    ) {
        log.log(Level.FINE, "Creating stopwatchRecordRepository");
        var folderWithData = stopwatchProperties.getFolderWithData();
        var path = Paths.get(folderWithData);
        return new StopwatchRecordFileRepository(path);
    }

    @NonNull
    public StopWatchTab stopWatchTab(
            @NonNull final StopwatchDatesVboxFactory stopwatchDatesVboxFactory,
            @NonNull final StopwatchPanelVBoxFactory stopwatchPanelVBoxFactory,
            @NonNull final StopwatchDateStatisticVBoxFactory stopwatchDateStatisticVBoxFactory
    ) {
        log.log(Level.FINE, "Creating stopWatchTab");
        return new StopWatchTab(stopwatchDatesVboxFactory, stopwatchPanelVBoxFactory, stopwatchDateStatisticVBoxFactory);
    }

    @NonNull
    public StopwatchDatesVboxFactory stopwatchDatesVboxFactory(
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final StopwatchRecordService stopwatchRecordService,
            @NonNull final StopwatchRecordRepository stopwatchRecordRepository,
            @NonNull final StopwatchProperties stopwatchProperties
    ) {
        log.log(Level.FINE, "Creating stopwatchDatesVboxFactory");
        var stopwatchDatesProperties = stopwatchProperties.getDates();
        return new StopwatchDatesVboxFactory(stopWatchAppState, stopwatchRecordService, stopwatchRecordRepository, stopwatchDatesProperties);
    }

    @NonNull
    public StopwatchPanelVBoxFactory stopwatchPanelVBoxFactory(
            @NonNull final StopwatchRecordService stopwatchRecordService,
            @NonNull final StopwatchRecordVBoxFactory stopwatchRecordVBoxFactory,
            @NonNull final RandomStopwatchRecordFactory randomStopwatchRecordFactory,
            @NonNull final StopwatchProperties stopwatchProperties
    ) {
        log.log(Level.FINE, "Creating stopwatchPanelVBoxFactory");
        return new StopwatchPanelVBoxFactory(stopwatchRecordService, randomStopwatchRecordFactory, stopwatchProperties, stopwatchRecordVBoxFactory);
    }

    @NonNull
    public StopwatchRecordVBoxFactory stopwatchRecordVBoxFactory(
            @NonNull final StopwatchRecordService stopwatchRecordService
    ) {
        log.log(Level.FINE, "Creating stopwatchRecordVBoxFactory");
        return new StopwatchRecordVBoxFactory(stopwatchRecordService);
    }

    @NonNull
    public StopwatchDateStatisticVBoxFactory stopwatchDateStatisticVBoxFactory(
            @NonNull final StopwatchRecordService stopwatchRecordService
    ) {
        log.log(Level.FINE, "Creating stopwatchDateStatisticVBoxFactory");
        return new StopwatchDateStatisticVBoxFactory(stopwatchRecordService);
    }


}
