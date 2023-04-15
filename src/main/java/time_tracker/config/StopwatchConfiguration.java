package time_tracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.mapper.RecordToStopwatchRecordConverter;
import time_tracker.model.mapper.StopwatchRecordToRecordConverter;
import time_tracker.repository.DayStatisticsRepository;
import time_tracker.repository.FileRepository;
import time_tracker.repository.StopwatchRecordRepository;
import time_tracker.service.*;
import time_tracker.service.dev.RandomStopwatchRecordFactory;

import java.util.logging.Level;

@Log
public class StopwatchConfiguration {
    private final RepositoryConfiguration repositoryConfiguration = new RepositoryConfiguration();

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
    public DayStatisticsService dayStatisticsService(@NonNull final DayStatisticsRepository dayStatisticsRepository) {
        log.log(Level.FINE, "Creating stopwatchRecordService");
        return GlobalContext.createStoreAndReturn(
                DayStatisticsService.class,
                () -> new DefaultDayStatisticsService(dayStatisticsRepository)
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
    public DayStatisticsRepository dayStatisticsRepository(
            @NonNull final FileRepository fileRepository
    ) {
        return repositoryConfiguration.dayStatisticsRepository(fileRepository);
    }

    @NonNull
    public StopwatchRecordRepository stopwatchRecordRepository(
            @NonNull final FileRepository fileRepository,
            @NonNull final StopwatchRecordToRecordConverter stopwatchRecordToRecordConverter,
            @NonNull final RecordToStopwatchRecordConverter recordToStopwatchRecordConverter
    ) {
        return repositoryConfiguration.stopwatchRecordRepository(fileRepository, stopwatchRecordToRecordConverter, recordToStopwatchRecordConverter);
    }

    @NonNull
    public FileRepository fileRepository(
            @NonNull final StopwatchProperties stopwatchProperties,
            @NonNull final ObjectMapper objectMapper
    ) {
        return repositoryConfiguration.fileRepository(stopwatchProperties, objectMapper);
    }

    @NonNull
    public StopwatchRecordToRecordConverter stopwatchRecordToRecordConverter() {
        return repositoryConfiguration.stopwatchRecordToRecordConverter();
    }

    @NonNull
    public RecordToStopwatchRecordConverter recordToStopwatchRecordConverter() {
        return repositoryConfiguration.recordToStopwatchRecordConverter();
    }

    @NonNull
    public TimeService timeService() {
        log.log(Level.FINE, "Creating timeService");
        return GlobalContext.createStoreAndReturn(TimeService::new);
    }

    @NonNull
    public StopwatchRecordSearchService stopwatchRecordSearchService() {
        log.log(Level.FINE, "Creating stopwatchRecordSearchService");
        return GlobalContext.createStoreAndReturn(
                StopwatchRecordSearchService.class,
                StopwatchRecordSearchServiceImpl::new
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
    public StopwatchMeasurementService stopwatchMeasurementService(
            @NonNull final StopWatchAppState stopWatchAppState
    ) {
        log.log(Level.FINE, "Creating stopwatchMeasurementService");
        return GlobalContext.createStoreAndReturn(
                StopwatchMeasurementService.class,
                () -> new DefaultStopwatchMeasurementService(stopWatchAppState)
        );
    }

    @NonNull
    public InitialDataLoadService initialDataLoadService(
            @NonNull final StopwatchRecordRepository stopwatchRecordRepository,
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final StopwatchRecordOnLoadFactory stopwatchRecordOnLoadFactory,
            @NonNull final DayDataService dayDataService,
            @NonNull final DayStatisticsService dayStatisticsService
    ) {
        log.log(Level.FINE, "Creating initialDataLoadService");
        return GlobalContext.createStoreAndReturn(
                InitialDataLoadService.class,
                () -> new InitialDataLoadService(stopwatchRecordRepository, stopWatchAppState, stopwatchRecordOnLoadFactory, dayDataService, dayStatisticsService)
        );
    }

    @NonNull
    public DayDataService dayDataService(
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final DayStatisticsService dayStatisticsService,
            @NonNull final StopwatchRecordService stopwatchRecordService
    ) {
        log.log(Level.FINE, "Creating dayDataService");
        return GlobalContext.createStoreAndReturn(
                DayDataService.class,
                () -> new DayDataService(stopWatchAppState, dayStatisticsService, stopwatchRecordService)
        );
    }
}
