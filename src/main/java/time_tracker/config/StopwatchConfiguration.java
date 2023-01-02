package time_tracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.mapper.MeasurementToStopwatchRecordMeasurementConverter;
import time_tracker.model.mapper.RecordToStopwatchRecordConverter;
import time_tracker.model.mapper.StopwatchRecordMeasurementToMeasurementConverter;
import time_tracker.model.mapper.StopwatchRecordToRecordConverter;
import time_tracker.repository.*;
import time_tracker.service.*;
import time_tracker.service.dev.RandomStopwatchRecordFactory;

import java.nio.file.Paths;
import java.util.logging.Level;

@Log
public class StopwatchConfiguration {

    @NonNull
    public StopwatchRecordService stopwatchRecordService(
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final StopwatchRecordRepository stopwatchRecordRepository
    ) {
        log.log(Level.FINE, "Creating stopwatchRecordService");
        return GlobalContext.createStoreAndReturn(
                StopwatchRecordService.class,
                () -> new DefaultStopwatchRecordService(stopWatchAppState, stopwatchRecordRepository)
        );
    }

    @NonNull
    public DayStatisticsService dayStatisticsService(
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final DayStatisticsRepository dayStatisticsRepository
    ) {
        log.log(Level.FINE, "Creating stopwatchRecordService");
        return GlobalContext.createStoreAndReturn(
                DayStatisticsService.class,
                () -> new DefaultDayStatisticsService(stopWatchAppState, dayStatisticsRepository)
        );
    }

    @NonNull
    public StopwatchRecordOnLoadFactory stopwatchRecordOnLoadFactory(
            @NonNull final StopwatchProperties stopwatchProperties,
            @NonNull final StopwatchRecordRepository stopwatchRecordRepository
    ) {
        log.log(Level.FINE, "Creating stopwatchRecordOnLoadFactory");
        return GlobalContext.createStoreAndReturn(
                StopwatchRecordOnLoadFactory.class,
                () -> new StopwatchRecordOnLoadFactoryImpl(stopwatchProperties, stopwatchRecordRepository)
        );
    }

    @NonNull
    public StopWatchAppState stopWatchAppState() {
        log.log(Level.FINE, "Creating stopWatchAppState");
        return GlobalContext.createStoreAndReturn(StopWatchAppState::new);
    }

    //TODO move to a dedicated repository configuration
    @NonNull
    public DayStatisticsRepository dayStatisticsRepository(
            @NonNull final FileRepository fileRepository
    ) {
        log.log(Level.FINE, "Creating dayStatisticsRepository");
        return GlobalContext.createStoreAndReturn(
                DayStatisticsRepository.class,
                () -> new DefaultDayStatisticsRepository(fileRepository)
        );
    }

    @NonNull
    public StopwatchRecordRepository stopwatchRecordRepository(
            @NonNull final FileRepository fileRepository,
            @NonNull final StopwatchRecordToRecordConverter stopwatchRecordToRecordConverter,
            @NonNull final RecordToStopwatchRecordConverter recordToStopwatchRecordConverter
    ) {
        log.log(Level.FINE, "Creating stopwatchRecordRepository");
        return GlobalContext.createStoreAndReturn(
                StopwatchRecordRepository.class,
                () -> {
                    var result = new StopwatchRecordFileRepository(stopwatchRecordToRecordConverter, recordToStopwatchRecordConverter, fileRepository);
                    result.loadAll();
                    return result;
                }
        );
    }

    @NonNull
    public FileRepository fileRepository(
            @NonNull final StopwatchProperties stopwatchProperties,
            @NonNull final ObjectMapper objectMapper
    ) {
        log.log(Level.FINE, "Creating fileRepository");
        var folderWithData = stopwatchProperties.getFolderWithData();
        var path = Paths.get(folderWithData);
        return GlobalContext.createStoreAndReturn(
                FileRepository.class,
                () -> new FileRepository(path, objectMapper)
        );
    }

    @NonNull
    public TimeService timeService() {
        log.log(Level.FINE, "Creating timeService");
        return GlobalContext.createStoreAndReturn(TimeService::new);
    }

    @NonNull
    public StopwatchRecordSearchService stopwatchRecordSearchService(
            @NonNull final StopwatchRecordRepository stopwatchRecordRepository
    ) {
        log.log(Level.FINE, "Creating stopwatchRecordSearchService");
        return GlobalContext.createStoreAndReturn(
                StopwatchRecordSearchService.class,
                () -> new StopwatchRecordSearchServiceImpl(
                        stopwatchRecordRepository
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

    @NonNull
    public ObjectMapper objectMapper() {
        log.log(Level.FINE, "Creating objectMapper");
        return GlobalContext.createStoreAndReturn(
                ObjectMapper.class,
                () -> {
                    var objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    return objectMapper;
                }
        );
    }

    @NonNull
    public StopwatchRecordToRecordConverter stopwatchRecordToRecordConverter() {
        log.log(Level.FINE, "Creating stopwatchRecordToRecordConverter");
        return GlobalContext.createStoreAndReturn(
                StopwatchRecordToRecordConverter.class,
                () -> {
                    var stopwatchRecordMeasurementToMeasurementConverter = new StopwatchRecordMeasurementToMeasurementConverter();
                    return new StopwatchRecordToRecordConverter(stopwatchRecordMeasurementToMeasurementConverter);
                }
        );
    }

    @NonNull
    public RecordToStopwatchRecordConverter recordToStopwatchRecordConverter() {
        log.log(Level.FINE, "Creating recordToStopwatchRecordConverter");
        return GlobalContext.createStoreAndReturn(
                RecordToStopwatchRecordConverter.class,
                () -> {
                    var measurementToStopwatchRecordMeasurementConverter = new MeasurementToStopwatchRecordMeasurementConverter();
                    return new RecordToStopwatchRecordConverter(measurementToStopwatchRecordMeasurementConverter);
                }
        );
    }

    @NonNull
    public StopwatchMeasurementService stopwatchMeasurementService(
            @NonNull final StopWatchAppState stopWatchAppState
    ) {
        log.log(Level.FINE, "Creating stopwatchMeasurementService");
        return GlobalContext.createStoreAndReturn(
                StopwatchMeasurementService.class,
                () -> new DefaultStopwatchMeasurementService(stopWatchAppState)
        );
    }
}
