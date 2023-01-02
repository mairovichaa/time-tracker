package time_tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.component.TimeTrackerTabPane;
import time_tracker.config.GlobalContext;
import time_tracker.config.StopwatchConfiguration;
import time_tracker.config.properties.AppProperties;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.domain.DayStatistics;
import time_tracker.model.DayData;
import time_tracker.repository.DayStatisticsRepository;
import time_tracker.repository.FileRepository;
import time_tracker.service.ChosenDateToRecordsForChosenDateBinder;
import time_tracker.service.DayStatisticsService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
public class TimeTrackerApp extends Application {

    public static Stage primaryStage;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        log.log(Level.INFO, "Starting application");

        var pathToPropertiesFile = System.getenv("pathToPropertiesFile");
        var appProperties = readAppProperties(pathToPropertiesFile);
        var stopwatchProperties = appProperties.getStopwatch();
        GlobalContext.put(StopwatchProperties.class, stopwatchProperties);

        var stopwatchConfiguration = new StopwatchConfiguration();

        var objectMapper = stopwatchConfiguration.objectMapper();
        var recordToStopwatchRecordConverter = stopwatchConfiguration.recordToStopwatchRecordConverter();
        var stopwatchRecordToRecordConverter = stopwatchConfiguration.stopwatchRecordToRecordConverter();

        var fileRepository = stopwatchConfiguration.fileRepository(stopwatchProperties, objectMapper);
        var stopwatchRecordRepository = stopwatchConfiguration.stopwatchRecordRepository(
                fileRepository,
                stopwatchRecordToRecordConverter,
                recordToStopwatchRecordConverter
        );
        var dayStatisticsRepository = stopwatchConfiguration.dayStatisticsRepository(fileRepository);

        var stopWatchAppState = stopwatchConfiguration.stopWatchAppState();
        var stopwatchRecordOnLoadFactory = stopwatchConfiguration.stopwatchRecordOnLoadFactory(stopwatchProperties, stopwatchRecordRepository);
        var stopwatchRecordService = stopwatchConfiguration.stopwatchRecordService(stopWatchAppState, stopwatchRecordRepository);
        var randomStopwatchRecordFactory = stopwatchConfiguration.randomStopwatchRecordFactory(stopwatchRecordService);
        var searchState = stopWatchAppState.getSearchState();
        var timeService = stopwatchConfiguration.timeService();

        var stopwatchRecordSearchService = stopwatchConfiguration.stopwatchRecordSearchService(stopwatchRecordRepository);
        stopwatchRecordSearchService.initialize(searchState);


        stopWatchAppState.setDateToRecords(stopwatchRecordRepository.getLoaded());

        var dateToDayData = stopWatchAppState.getDateToRecords()
                .entrySet()
                .stream()
                .map(it -> new DayData(it.getKey(), it.getValue()))
                .collect(Collectors.toMap(DayData::getDate, x -> x));

        var dayStatisticsService = stopwatchConfiguration.dayStatisticsService(stopWatchAppState, dayStatisticsRepository);
        dayStatisticsService.enrich(dateToDayData);
        stopWatchAppState.setDateToDayData(dateToDayData);

        var chosenDateToRecordsForChosenDateBinder = new ChosenDateToRecordsForChosenDateBinder(stopWatchAppState, stopwatchRecordOnLoadFactory);
        chosenDateToRecordsForChosenDateBinder.bind();

        stopWatchAppState.setChosenDate(LocalDate.now());

        stopwatchConfiguration.stopwatchMeasurementService(stopWatchAppState);

        TabPane tabPane = new TimeTrackerTabPane();
        Scene scene = new Scene(tabPane, 600, 600);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        TimeTrackerApp.primaryStage = primaryStage;
    }

    private AppProperties readAppProperties(@NonNull final String pathToPropertiesFile) {
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
}
