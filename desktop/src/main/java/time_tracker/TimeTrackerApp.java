package time_tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.extern.java.Log;
import time_tracker.common.GlobalContext;
import time_tracker.component.AppHBox;
import time_tracker.config.StopwatchConfiguration;
import time_tracker.config.properties.AppProperties;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.config.report.GroupedByDateConfiguration;
import time_tracker.config.report.GroupedByRecordConfiguration;
import time_tracker.config.report.ReportConfiguration;
import time_tracker.model.ReportState;
import time_tracker.model.StopWatchAppState;
import time_tracker.service.StopwatchRecordSearchService;
import time_tracker.service.report.ReportGenerator;
import time_tracker.service.report.ReportService;
import time_tracker.service.report.common.StopwatchMeasurementExportDtoCustomFormatWriter;
import time_tracker.service.report.common.StopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper;
import time_tracker.service.report.groupedByDate.CustomFormatWriter;
import time_tracker.service.report.groupedByDate.GroupedByDateReportGenerator;
import time_tracker.service.report.groupedByRecord.GroupedByRecordReportGenerator;

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
        var stopwatchProperties = appProperties.getStopwatch();
        GlobalContext.put(StopwatchProperties.class, stopwatchProperties);

        var objectMapper = stopwatchConfiguration.objectMapper();
        var objectWriter = stopwatchConfiguration.objectWriter(objectMapper);

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
        var yamlObjectMapper = new ObjectMapper(new YAMLFactory());

        var configurationService = stopwatchConfiguration.configurationService(appProperties, pathToPropertiesFile, yamlObjectMapper);

        stopwatchConfiguration.stopwatchMeasurementService(stopWatchAppState);

        var dayStatisticsService = stopwatchConfiguration.dayStatisticsService(dayStatisticsRepository);

        var dayDataService = stopwatchConfiguration.dayDataService(stopWatchAppState, dayStatisticsService, stopwatchRecordService);
        var initialDataLoadService = stopwatchConfiguration.initialDataLoadService(stopwatchRecordService, stopWatchAppState, stopwatchRecordOnLoadFactory, dayDataService, dayStatisticsService);
        initialDataLoadService.load();

        stopWatchAppState.setChosenDate(timeService.today());

        var stopwatchRecordSearchService = stopwatchConfiguration.stopwatchRecordSearchService();
        stopwatchRecordSearchService.initialize(searchState, stopWatchAppState, stopwatchRecordSearchService);

        runReportConfiguration(appProperties);

        appHBox = new AppHBox();
        Scene scene = new Scene(appHBox, 600, 600);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);

        primaryStage.show();
        primaryStage.setMaximized(true);
        appHBox.init(primaryStage);
    }


    private void runReportConfiguration(@NonNull final AppProperties appProperties) {
        var reportConfiguration = new ReportConfiguration();
        ReportState reportState = reportConfiguration.reportState(appProperties);
        StopwatchMeasurementExportDtoCustomFormatWriter stopwatchMeasurementExportDtoCustomFormatWriter = reportConfiguration.stopwatchMeasurementExportDtoCustomFormatWriter(reportState);
        StopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper stopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper = reportConfiguration.stopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper(reportState);

        ObjectWriter objectWriter = GlobalContext.get(ObjectWriter.class);

        GroupedByDateConfiguration groupedByDateConfiguration = new GroupedByDateConfiguration();
        CustomFormatWriter groupedByDateCustomFormatWriter = groupedByDateConfiguration.customFormatMapper(stopwatchMeasurementExportDtoCustomFormatWriter);
        time_tracker.service.report.groupedByDate.ExportDtoMappers groupedByDateExportDtoMappers = groupedByDateConfiguration.exportDtoMappers(stopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper);
        GroupedByDateReportGenerator groupedByDateReportGenerator = groupedByDateConfiguration.groupedByDateReportGenerator(groupedByDateCustomFormatWriter, objectWriter, groupedByDateExportDtoMappers);

        GroupedByRecordConfiguration groupedByRecordConfiguration = new GroupedByRecordConfiguration();
        time_tracker.service.report.groupedByRecord.CustomFormatWriter groupedByRecordCustomFormatWriter = groupedByRecordConfiguration.customFormatMapper(stopwatchMeasurementExportDtoCustomFormatWriter);
        time_tracker.service.report.groupedByRecord.ExportDtoMappers groupedByRecordExportDtoMappers = groupedByRecordConfiguration.exportDtoMappers(stopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper);
        GroupedByRecordReportGenerator groupedByRecordReportGenerator = groupedByRecordConfiguration.groupedByRecordReportGenerator(groupedByRecordCustomFormatWriter, objectWriter, groupedByRecordExportDtoMappers);

        ReportGenerator reportGenerator = reportConfiguration.reportGenerator(groupedByDateReportGenerator, groupedByRecordReportGenerator);

        StopWatchAppState stopWatchAppState = GlobalContext.get(StopWatchAppState.class);
        ReportService reportService = reportConfiguration.reportService(reportState, stopWatchAppState);
        reportService.init();

        reportConfiguration.reportExporter(reportState, reportGenerator);
    }

    public static void showStopwatch() {
        appHBox.showStopwatch();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        var stopwatchRecordSearchService = GlobalContext.get(StopwatchRecordSearchService.class);
        stopwatchRecordSearchService.shutdown();
    }
}
