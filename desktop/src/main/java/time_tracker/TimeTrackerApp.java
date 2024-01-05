package time_tracker;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import time_tracker.common.GlobalContext;
import time_tracker.component.TimeTrackerTabPane;
import time_tracker.config.StopwatchConfiguration;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.service.StopwatchRecordSearchService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Stream;

@Log
public class TimeTrackerApp extends Application {

    public static Stage primaryStage;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        log.log(Level.INFO, "Starting application");

        var stopwatchConfiguration = new StopwatchConfiguration();

        var pathToPropertiesFile = System.getenv("pathToPropertiesFile");
        var appProperties = stopwatchConfiguration.appProperties(pathToPropertiesFile);
        var stopwatchProperties = appProperties.getStopwatch();
        GlobalContext.put(StopwatchProperties.class, stopwatchProperties);

        var objectMapper = stopwatchConfiguration.objectMapper();
        var recordToStopwatchRecordConverter = stopwatchConfiguration.recordToStopwatchRecordConverter();
        var stopwatchRecordToRecordConverter = stopwatchConfiguration.stopwatchRecordToRecordConverter();

        // TODO move to separate service???
        var folderWithData = stopwatchProperties.getFolderWithData();
        var path = Paths.get(folderWithData);
        Stream.of("records", "day-statistics")
                .map(it -> it + ".json")
                .map(path::resolve)
                .map(Path::toFile)
                .filter(Predicate.not(File::exists))
                .forEach(it -> {
                    try {
                        Files.writeString(it.toPath(), "[]");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        var fileRepository = stopwatchConfiguration.fileRepository(stopwatchProperties, objectMapper);
        var stopwatchRecordRepository = stopwatchConfiguration.stopwatchRecordRepository(
                fileRepository,
                stopwatchRecordToRecordConverter,
                recordToStopwatchRecordConverter
        );
        var dayStatisticsRepository = stopwatchConfiguration.dayStatisticsRepository(fileRepository);

        var stopWatchAppState = stopwatchConfiguration.stopWatchAppState();
        var stopwatchRecordOnLoadFactory = stopwatchConfiguration.stopwatchRecordOnLoadFactory(stopwatchProperties, stopwatchRecordRepository);
        var stopwatchRecordService = stopwatchConfiguration.stopwatchRecordService(stopWatchAppState, stopwatchRecordRepository, stopwatchRecordOnLoadFactory, stopwatchRecordToRecordConverter, recordToStopwatchRecordConverter);
        var randomStopwatchRecordFactory = stopwatchConfiguration.randomStopwatchRecordFactory(stopwatchRecordService);
        var searchState = stopWatchAppState.getSearchState();
        var timeService = stopwatchConfiguration.timeService();
        var appStateService = stopwatchConfiguration.appStateService(stopwatchRecordService, stopWatchAppState);
        var configurationService = stopwatchConfiguration.configurationService(appProperties, pathToPropertiesFile);

        stopwatchConfiguration.stopwatchMeasurementService(stopWatchAppState);

        var dayStatisticsService = stopwatchConfiguration.dayStatisticsService(dayStatisticsRepository);

        var dayDataService = stopwatchConfiguration.dayDataService(stopWatchAppState, dayStatisticsService, stopwatchRecordService);
        var initialDataLoadService = stopwatchConfiguration.initialDataLoadService(stopwatchRecordService, stopWatchAppState, stopwatchRecordOnLoadFactory, dayDataService, dayStatisticsService);
        initialDataLoadService.load();

        stopWatchAppState.setChosenDate(timeService.today());

        var stopwatchRecordSearchService = stopwatchConfiguration.stopwatchRecordSearchService();
        stopwatchRecordSearchService.initialize(searchState, stopWatchAppState, stopwatchRecordSearchService);

        TabPane tabPane = new TimeTrackerTabPane();
        Scene scene = new Scene(tabPane, 600, 600);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        TimeTrackerApp.primaryStage = primaryStage;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        var stopwatchRecordSearchService = GlobalContext.get(StopwatchRecordSearchService.class);
        stopwatchRecordSearchService.shutdown();
    }
}
