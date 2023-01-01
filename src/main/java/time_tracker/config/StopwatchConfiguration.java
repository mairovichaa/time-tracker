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
import time_tracker.repository.StopwatchRecordFileRepository;
import time_tracker.repository.StopwatchRecordRepository;
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

    @NonNull
    public StopwatchRecordRepository stopwatchRecordRepository(
            @NonNull final StopwatchProperties stopwatchProperties,
            @NonNull final ObjectMapper objectMapper,
            @NonNull final StopwatchRecordToRecordConverter stopwatchRecordToRecordConverter,
            @NonNull final RecordToStopwatchRecordConverter recordToStopwatchRecordConverter
    ) {
        log.log(Level.FINE, "Creating stopwatchRecordRepository");
        var folderWithData = stopwatchProperties.getFolderWithData();
        var path = Paths.get(folderWithData);
        return GlobalContext.createStoreAndReturn(
                StopwatchRecordRepository.class,
                () -> {
                    var result = new StopwatchRecordFileRepository(path, objectMapper, stopwatchRecordToRecordConverter, recordToStopwatchRecordConverter);

                    result.loadAll();
                    return result;
                }
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
