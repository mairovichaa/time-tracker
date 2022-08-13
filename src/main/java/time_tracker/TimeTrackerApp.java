package time_tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.component.interval.IntervalTab;
import time_tracker.component.search.SearchTab;
import time_tracker.config.GlobalContext;
import time_tracker.config.StopwatchConfiguration;
import time_tracker.config.properties.AppProperties;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.service.ChosenDateToRecordsForChosenDateBinder;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;

@Log
public class TimeTrackerApp extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        log.log(Level.INFO, "Starting application");
        // TODO move to a separate Tab component
        TabPane tabPane = new TabPane();

        var pathToPropertiesFile = System.getenv("pathToPropertiesFile");
        var appProperties = readAppProperties(pathToPropertiesFile);
        var stopwatchProperties = appProperties.getStopwatch();
        GlobalContext.put(StopwatchProperties.class, stopwatchProperties);

        var stopwatchConfiguration = new StopwatchConfiguration();
        var stopwatchRecordRepository = stopwatchConfiguration.stopwatchRecordRepository(stopwatchProperties);
        var stopWatchAppState = stopwatchConfiguration.stopWatchAppState();
        var stopwatchRecordOnLoadFactory = stopwatchConfiguration.stopwatchRecordOnLoadFactory(stopwatchProperties);
        var stopwatchRecordService = stopwatchConfiguration.stopwatchRecordService(stopWatchAppState, stopwatchRecordRepository);
        var randomStopwatchRecordFactory = stopwatchConfiguration.randomStopwatchRecordFactory(stopwatchRecordService);
        var searchState = stopWatchAppState.getSearchState();
        var stopWatchTab = stopwatchConfiguration.stopWatchTab();

        var stopwatchRecordSearchService = stopwatchConfiguration.stopwatchRecordSearchService(stopwatchRecordRepository, stopWatchAppState, stopwatchProperties);
        stopwatchRecordSearchService.initialize(searchState);

        var searchTab = new SearchTab();

        // TODO get rid of it?
        stopWatchAppState.setDateToRecords(stopwatchRecordRepository.getLoaded());

        var chosenDateToRecordsForChosenDateBinder = new ChosenDateToRecordsForChosenDateBinder(stopWatchAppState, stopwatchRecordOnLoadFactory);
        chosenDateToRecordsForChosenDateBinder.bind();

        stopWatchAppState.setChosenDate(LocalDate.now());

        Tab intervalTab = new IntervalTab();

        tabPane.getTabs().addAll(stopWatchTab, searchTab, intervalTab);
        Scene scene = new Scene(tabPane, 600, 600);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
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
