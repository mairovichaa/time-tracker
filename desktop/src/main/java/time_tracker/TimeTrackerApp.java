package time_tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import time_tracker.common.di.DiContext;
import time_tracker.component.AppHBox;
import time_tracker.config.CommonConfiguration;
import time_tracker.config.StopwatchConfiguration;
import time_tracker.config.properties.AppProperties;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.config.report.GroupedByDateConfiguration;
import time_tracker.config.report.GroupedByRecordConfiguration;
import time_tracker.config.report.MeasurementReportConfiguration;
import time_tracker.config.report.ReportConfiguration;
import time_tracker.configuration.RepositoryConfiguration;
import time_tracker.model.StopWatchAppState;
import time_tracker.repository.FileRepository;
import time_tracker.service.StopwatchRecordSearchService;
import time_tracker.service.TimeService;

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

    public static DiContext CONTEXT;
    public static Stage primaryStage;

    private static AppHBox appHBox;

    public static void main(String[] args) {
        launch();
    }

    public static void onMainWindowSizeChange(ChangeListener<Number> listener) {
        primaryStage.heightProperty().addListener(listener);
    }

    public static void onMainWindowHeightSizeChange(ChangeListener<Number> listener) {
        primaryStage.getScene().heightProperty().addListener(listener);
    }

    public static void onMainWindowWidthSizeChange(ChangeListener<Number> listener) {
        primaryStage.getScene().widthProperty().addListener(listener);
    }

    @Override
    public void start(Stage primaryStage) {
        TimeTrackerApp.primaryStage = primaryStage;
        log.log(Level.INFO, "Starting application");

        var stopwatchConfiguration = new StopwatchConfiguration();

        var pathToPropertiesFile = System.getenv("pathToPropertiesFile");
        var appProperties = stopwatchConfiguration.appProperties(pathToPropertiesFile);

        String folderWithData = appProperties.getStopwatch().getFolderWithData();
        // TODO move to separate service???
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

        TimeTrackerApp.CONTEXT = new DiContext();

        CONTEXT.register(CommonConfiguration.class);
        CONTEXT.register(AppProperties.class, appProperties);
        CONTEXT.register(StopwatchProperties.class, appProperties.getStopwatch());

        ObjectMapper objectMapperFromContext = CONTEXT.get(ObjectMapper.class);
        FileRepository fileRepository = new FileRepository(Paths.get(folderWithData), objectMapperFromContext);
        CONTEXT.register(FileRepository.class, fileRepository);
        CONTEXT.register(RepositoryConfiguration.class);
        CONTEXT.register(StopwatchConfiguration.class);
        CONTEXT.register(MeasurementReportConfiguration.class);
        CONTEXT.register(GroupedByDateConfiguration.class);
        CONTEXT.register(GroupedByRecordConfiguration.class);
        CONTEXT.register(ReportConfiguration.class);

        StopWatchAppState stopWatchAppState = CONTEXT.get(StopWatchAppState.class);
        TimeService timeService = CONTEXT.get(TimeService.class);
        stopWatchAppState.setChosenDate(timeService.today());

        appHBox = new AppHBox();
        Scene scene = new Scene(appHBox, 600, 600);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
        stopWatchAppState.setChosenWorkspace(AppHBox.WorkspaceItem.REPORT);

        primaryStage.show();
        primaryStage.setMaximized(true);
        appHBox.init(primaryStage);
    }

    public static void showStopwatch() {
        appHBox.showStopwatch();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        var stopwatchRecordSearchService = CONTEXT.get(StopwatchRecordSearchService.class);
        stopwatchRecordSearchService.shutdown();
    }
}
