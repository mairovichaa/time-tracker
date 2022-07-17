package time_tracker.config;

import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.component.stopwatch.StopWatchTab;
import time_tracker.component.stopwatch.factory.*;
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
        return new DefaultStopwatchRecordService(stopWatchAppState, stopwatchRecordRepository, stopwatchRecordOnLoadFactory);
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
            @NonNull final StopwatchDateStatisticVBoxFactory stopwatchDateStatisticVBoxFactory,
            @NonNull final StopwatchSearchVboxFactory stopwatchSearchVboxFactory
    ) {
        log.log(Level.FINE, "Creating stopWatchTab");
        return new StopWatchTab(stopwatchDatesVboxFactory, stopwatchPanelVBoxFactory, stopwatchDateStatisticVBoxFactory, stopwatchSearchVboxFactory);
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
    public StopwatchSearchVboxFactory stopwatchSearchVboxFactory(
            @NonNull final StopwatchSearchState stopwatchSearchState
    ) {
        log.log(Level.FINE, "Creating StopwatchSearchVboxFactory");
        return new StopwatchSearchVboxFactory(stopwatchSearchState);
    }

    @NonNull
    public StopwatchDateStatisticVBoxFactory stopwatchDateStatisticVBoxFactory(
            @NonNull final StopwatchRecordService stopwatchRecordService
    ) {
        log.log(Level.FINE, "Creating stopwatchDateStatisticVBoxFactory");
        return new StopwatchDateStatisticVBoxFactory(stopwatchRecordService);
    }

    @NonNull
    public StopwatchRecordSearchService stopwatchRecordSearchService(
            @NonNull final StopwatchRecordRepository stopwatchRecordRepository,
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final StopwatchProperties stopwatchProperties
    ) {
        log.log(Level.FINE, "Creating stopwatchRecordSearchService");

        return new StopwatchRecordSearchServiceImpl(
                stopwatchRecordRepository,
                stopWatchAppState.getChosenDate(),
                stopwatchProperties
        );

    }

}
