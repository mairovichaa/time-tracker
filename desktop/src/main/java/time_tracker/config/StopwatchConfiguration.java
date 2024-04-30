package time_tracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.common.di.Bean;
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
    @Bean
    public AppStateService appStateService(
            @NonNull final StopwatchRecordService stopwatchRecordService,
            @NonNull final StopWatchAppState stopWatchAppState) {
        log.log(Level.FINE, "Creating appStateService");
        return new AppStateService(stopwatchRecordService, stopWatchAppState);
    }

    @NonNull
    @Bean
    public StopwatchRecordService stopwatchRecordService(
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final StopwatchRecordRepository stopwatchRecordRepository,
            @NonNull final StopwatchRecordOnLoadFactory stopwatchRecordOnLoadFactory,
            @NonNull final StopwatchRecordToRecordConverter stopwatchRecordToRecordConverter,
            @NonNull final RecordToStopwatchRecordConverter recordToStopwatchRecordConverter
    ) {
        log.log(Level.FINE, "Creating stopwatchRecordService");
        var service = new DefaultStopwatchRecordService(
                stopWatchAppState, stopwatchRecordRepository,
                stopwatchRecordOnLoadFactory,
                stopwatchRecordToRecordConverter, recordToStopwatchRecordConverter);
        service.loadAll();
        return service;

    }

    @NonNull
    @Bean
    public DayStatisticsService dayStatisticsService(@NonNull final DayStatisticsRepository dayStatisticsRepository) {
        log.log(Level.FINE, "Creating stopwatchRecordService");
        return new DefaultDayStatisticsService(dayStatisticsRepository);
    }

    @NonNull
    @Bean
    public StopwatchRecordOnLoadFactory stopwatchRecordOnLoadFactory(
            @NonNull final StopwatchProperties stopwatchProperties,
            @NonNull final StopwatchRecordRepository stopwatchRecordRepository
    ) {
        log.log(Level.FINE, "Creating stopwatchRecordOnLoadFactory");
        return new StopwatchRecordOnLoadFactoryImpl(stopwatchProperties, stopwatchRecordRepository);
    }

    @NonNull
    @Bean
    public StopWatchAppState stopWatchAppState() {
        log.log(Level.FINE, "Creating stopWatchAppState");
        return new StopWatchAppState();
    }

    @NonNull
    @Bean
    public DayStatisticsRepository dayStatisticsRepository(
            @NonNull final FileRepository fileRepository
    ) {
        return repositoryConfiguration.dayStatisticsRepository(fileRepository);
    }

    @NonNull
    @Bean
    public StopwatchRecordRepository stopwatchRecordRepository(
            @NonNull final FileRepository fileRepository,
            // TODO remove
            @NonNull final StopwatchRecordToRecordConverter stopwatchRecordToRecordConverter,
            @NonNull final RecordToStopwatchRecordConverter recordToStopwatchRecordConverter
    ) {
        return repositoryConfiguration.stopwatchRecordRepository(fileRepository);
    }

    @NonNull
    @Bean
    public FileRepository fileRepository(
            @NonNull final StopwatchProperties stopwatchProperties,
            @NonNull final ObjectMapper objectMapper
    ) {
        return repositoryConfiguration.fileRepository(stopwatchProperties.getFolderWithData(), objectMapper);
    }

    @NonNull
    @Bean
    public StopwatchRecordToRecordConverter stopwatchRecordToRecordConverter() {
        log.log(Level.FINE, "Creating stopwatchRecordToRecordConverter");
        var measurementConverter = new StopwatchRecordMeasurementToMeasurementConverter();
        return new StopwatchRecordToRecordConverter(measurementConverter);
    }

    @NonNull
    @Bean
    public RecordToStopwatchRecordConverter recordToStopwatchRecordConverter() {
        log.log(Level.FINE, "Creating recordToStopwatchRecordConverter");
        var measurementConverter = new MeasurementToStopwatchRecordMeasurementConverter();
        return new RecordToStopwatchRecordConverter(measurementConverter);
    }

    @NonNull
    @Bean
    public TimeService timeService() {
        log.log(Level.FINE, "Creating timeService");
        return new TimeService();
    }

    @NonNull
    @Bean(initMethod = "initialize")
    public StopwatchRecordSearchService stopwatchRecordSearchService(@NonNull final StopWatchAppState stopWatchAppState) {
        log.log(Level.FINE, "Creating stopwatchRecordSearchService");
        return new StopwatchRecordSearchServiceImpl(stopWatchAppState);
    }

    @NonNull
    @Bean
    public RandomStopwatchRecordFactory randomStopwatchRecordFactory(
            @NonNull final StopwatchRecordService stopwatchRecordService
    ) {
        log.log(Level.FINE, "Creating randomStopwatchRecordFactory");
        return new RandomStopwatchRecordFactory(stopwatchRecordService);
    }

    @NonNull
    @Bean
    public ObjectWriter objectWriter(@NonNull final ObjectMapper objectMapper) {
        log.log(Level.FINE, "Creating objectWriter");
        return objectMapper.writerWithDefaultPrettyPrinter();
    }

    @NonNull
    @Bean
    public StopwatchMeasurementService stopwatchMeasurementService(
            @NonNull final StopWatchAppState stopWatchAppState
    ) {
        log.log(Level.FINE, "Creating stopwatchMeasurementService");
        return new DefaultStopwatchMeasurementService(stopWatchAppState);
    }

    @NonNull
    @Bean(initMethod = "load")
    public InitialDataLoadService initialDataLoadService(
            @NonNull final StopwatchRecordService stopwatchRecordService,
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final StopwatchRecordOnLoadFactory stopwatchRecordOnLoadFactory,
            @NonNull final DayDataService dayDataService,
            @NonNull final DayStatisticsService dayStatisticsService
    ) {
        log.log(Level.FINE, "Creating initialDataLoadService");
        return new InitialDataLoadService(stopwatchRecordService, stopWatchAppState, stopwatchRecordOnLoadFactory, dayDataService, dayStatisticsService);
    }

    @NonNull
    @Bean
    public DayDataService dayDataService(
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final DayStatisticsService dayStatisticsService,
            @NonNull final StopwatchRecordService stopwatchRecordService
    ) {
        log.log(Level.FINE, "Creating dayDataService");
        return new DayDataService(stopWatchAppState, dayStatisticsService, stopwatchRecordService);
    }

    @NonNull
    @Bean
    public AppProperties appProperties(String pathToPropertiesFile) {
        log.log(Level.INFO, () -> "Trying to read properties from " + pathToPropertiesFile);
        var objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            // TODO add default configs
            var propertiesFile = new File(pathToPropertiesFile);
            return objectMapper.readValue(propertiesFile, AppProperties.class);
        } catch (IOException e) {
            log.severe("Can't read properties by path: " + pathToPropertiesFile);
            throw new RuntimeException(e);
        }
    }

    @NonNull
    @Bean
    public ConfigurationService configurationService(
            @NonNull final AppProperties appProperties,
            @NonNull final String pathToPropertiesFile,
            @NonNull final ObjectMapper yamlObjectMapper
    ) {
        log.log(Level.FINE, "Creating configurationService");
        return new ConfigurationService(appProperties, pathToPropertiesFile, yamlObjectMapper);
    }
}
