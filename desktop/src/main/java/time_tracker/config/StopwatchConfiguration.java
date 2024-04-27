package time_tracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.java.Log;
import time_tracker.common.GlobalContext;
import time_tracker.common.annotation.NonNull;
import time_tracker.config.properties.AppProperties;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.configuration.RepositoryConfiguration;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.mapper.MeasurementToStopwatchRecordMeasurementConverter;
import time_tracker.model.mapper.RecordToStopwatchRecordConverter;
import time_tracker.model.mapper.StopwatchRecordMeasurementToMeasurementConverter;
import time_tracker.model.mapper.StopwatchRecordToRecordConverter;
import time_tracker.repository.DayStatisticsRepository;
import time_tracker.repository.FileRepository;
import time_tracker.repository.StopwatchRecordRepository;
import time_tracker.service.*;
import time_tracker.service.dev.RandomStopwatchRecordFactory;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

@Log
public class StopwatchConfiguration {
    private final RepositoryConfiguration repositoryConfiguration = new RepositoryConfiguration();

    @NonNull
    public AppStateService appStateService(
            @NonNull final StopwatchRecordService stopwatchRecordService,
            @NonNull final StopWatchAppState stopWatchAppState) {
        log.log(Level.FINE, "Creating appStateService");
        return GlobalContext.createStoreAndReturn(
                AppStateService.class,
                () -> new AppStateService(stopwatchRecordService, stopWatchAppState)
        );
    }

    @NonNull
    public StopwatchRecordService stopwatchRecordService(
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final StopwatchRecordRepository stopwatchRecordRepository,
            @NonNull final StopwatchRecordOnLoadFactory stopwatchRecordOnLoadFactory,
            @NonNull final StopwatchRecordToRecordConverter stopwatchRecordToRecordConverter,
            @NonNull final RecordToStopwatchRecordConverter recordToStopwatchRecordConverter
    ) {
        log.log(Level.FINE, "Creating stopwatchRecordService");
        return GlobalContext.createStoreAndReturn(
                StopwatchRecordService.class,
                () -> {
                    var service = new DefaultStopwatchRecordService(
                            stopWatchAppState, stopwatchRecordRepository,
                            stopwatchRecordOnLoadFactory,
                            stopwatchRecordToRecordConverter, recordToStopwatchRecordConverter);
                    service.loadAll();
                    return service;
                }
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
            // TODO remove
            @NonNull final StopwatchRecordToRecordConverter stopwatchRecordToRecordConverter,
            @NonNull final RecordToStopwatchRecordConverter recordToStopwatchRecordConverter
    ) {
        return repositoryConfiguration.stopwatchRecordRepository(fileRepository);
    }

    @NonNull
    public FileRepository fileRepository(
            @NonNull final StopwatchProperties stopwatchProperties,
            @NonNull final ObjectMapper objectMapper
    ) {
        return repositoryConfiguration.fileRepository(stopwatchProperties.getFolderWithData(), objectMapper);
    }

    @NonNull
    public StopwatchRecordToRecordConverter stopwatchRecordToRecordConverter() {
        log.log(Level.FINE, "Creating stopwatchRecordToRecordConverter");
        return GlobalContext.createStoreAndReturn(StopwatchRecordToRecordConverter.class, () -> {
            var measurementConverter = new StopwatchRecordMeasurementToMeasurementConverter();
            return new StopwatchRecordToRecordConverter(measurementConverter);
        });
    }

    @NonNull
    public RecordToStopwatchRecordConverter recordToStopwatchRecordConverter() {
        log.log(Level.FINE, "Creating recordToStopwatchRecordConverter");
        return GlobalContext.createStoreAndReturn(RecordToStopwatchRecordConverter.class, () -> {
            var measurementConverter = new MeasurementToStopwatchRecordMeasurementConverter();
            return new RecordToStopwatchRecordConverter(measurementConverter);
        });
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
    public ObjectWriter objectWriter(@NonNull final ObjectMapper objectMapper) {
        log.log(Level.FINE, "Creating objectWriter");
        return GlobalContext.createStoreAndReturn(
                ObjectWriter.class,
                objectMapper::writerWithDefaultPrettyPrinter
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
            @NonNull final StopwatchRecordService stopwatchRecordService,
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final StopwatchRecordOnLoadFactory stopwatchRecordOnLoadFactory,
            @NonNull final DayDataService dayDataService,
            @NonNull final DayStatisticsService dayStatisticsService
    ) {
        log.log(Level.FINE, "Creating initialDataLoadService");
        return GlobalContext.createStoreAndReturn(
                InitialDataLoadService.class,
                () -> new InitialDataLoadService(stopwatchRecordService, stopWatchAppState, stopwatchRecordOnLoadFactory, dayDataService, dayStatisticsService)
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

    @NonNull
    public AppProperties appProperties(String pathToPropertiesFile) {
        log.log(Level.INFO, () -> "Trying to read properties from " + pathToPropertiesFile);
        var objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            // TODO add default configs
            var propertiesFile = new File(pathToPropertiesFile);
            var appProperties = objectMapper.readValue(propertiesFile, AppProperties.class);
            return GlobalContext.createStoreAndReturn(AppProperties.class, () -> appProperties);
        } catch (IOException e) {
            log.severe("Can't read properties by path: " + pathToPropertiesFile);
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public ConfigurationService configurationService(
            @NonNull final AppProperties appProperties,
            @NonNull final String pathToPropertiesFile,
            @NonNull final ObjectMapper yamlObjectMapper
    ) {
        log.log(Level.FINE, "Creating configurationService");
        return GlobalContext.createStoreAndReturn(ConfigurationService.class, () -> new ConfigurationService(appProperties, pathToPropertiesFile, yamlObjectMapper));
    }
}
