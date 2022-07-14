package time_tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.component.Interval;
import time_tracker.config.StopwatchConfiguration;
import time_tracker.config.properties.AppProperties;
import time_tracker.service.dev.RandomStopwatchRecordFactory;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
public class TimeTrackerApp extends Application {

    private VBox entriesTable;

    private Button addBtn;

    private DoubleProperty total = new SimpleDoubleProperty(0);
    private Text totalText = new Text();

    private ClipboardContent clipboardContent = new ClipboardContent();
    private Clipboard clipboard = Clipboard.getSystemClipboard();


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        log.log(Level.INFO, "Starting application");
        // TODO move to a separate Tab component
        TabPane tabPane = new TabPane();
        Tab tab = new Tab();
        tab.setText("Interval counter");

        var pathToPropertiesFile = System.getenv("pathToPropertiesFile");
        var appProperties = readAppProperties(pathToPropertiesFile);
        var stopwatchProperties = appProperties.getStopwatch();

        var stopwatchConfiguration = new StopwatchConfiguration();
        var stopwatchRecordRepository = stopwatchConfiguration.stopwatchRecordRepository(stopwatchProperties);
        var stopWatchAppState = stopwatchConfiguration.stopWatchAppState();
        var stopwatchRecordService = stopwatchConfiguration.stopwatchRecordService(stopWatchAppState, stopwatchRecordRepository);
        var randomStopwatchRecordFactory = new RandomStopwatchRecordFactory(stopwatchRecordService);
        var stopwatchDatesVboxFactory = stopwatchConfiguration.stopwatchDatesVboxFactory(
                stopWatchAppState, stopwatchRecordService, stopwatchRecordRepository, stopwatchProperties
        );
        var stopwatchRecordVBoxFactory = stopwatchConfiguration.stopwatchRecordVBoxFactory(stopwatchRecordService);
        var stopwatchPanelVBoxFactory = stopwatchConfiguration.stopwatchPanelVBoxFactory(stopwatchRecordService, stopwatchRecordVBoxFactory, randomStopwatchRecordFactory, stopwatchProperties);
        var stopWatchTab = stopwatchConfiguration.stopWatchTab(stopwatchDatesVboxFactory, stopwatchPanelVBoxFactory);
        tabPane.getTabs().addAll(stopWatchTab, tab);

        totalText.textProperty().bind(Bindings.concat("Total : ", total.asString("%.2f")));

        Text startTitle = new Text("Start");
        Text endTitle = new Text("End");

        HBox entriesTitleRow = new HBox(startTitle, endTitle);
        entriesTable = new VBox(entriesTitleRow);

        addBtn = new Button("Add");
        addBtn.setOnAction(e -> {
            addCurrentRow();
            createTimeRow();
        });

        Button copyBtn = new Button("Copy");
        copyBtn.setOnAction(e -> {
            String stringForCopy = createStringForCopy();
            log.log(Level.INFO, () -> "String for copy: " + stringForCopy);
            clipboardContent.putString(stringForCopy);
            clipboard.setContent(clipboardContent);
        });

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> {
            entriesTable.getChildren().clear();
            entriesTable.getChildren().add(entriesTitleRow);
            total.setValue(0.0);
            createTimeRow();
        });

        createTimeRow();

        HBox controls = new HBox(addBtn, copyBtn, refreshBtn);
        controls.setSpacing(5);

        VBox root = new VBox(totalText, entriesTable, controls);
        root.setSpacing(5);

        tab.setContent(root);
        Scene scene = new Scene(tabPane, 600, 600);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
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

    private String createStringForCopy() {
        String rangeString = entriesTable.getChildren()
                .subList(1, entriesTable.getChildren().size() - 1)
                .stream()
                .map(node -> (Interval) node)
                .map(Interval::asStringForExcel)
                .collect(Collectors.joining("\t"));
        return total.getValue().toString().replace(".", ",") + "\t" + rangeString;
    }

    private void addCurrentRow() {
        Interval current = retrieveCurrent();
        current.disableInputs();
        double diff = current.calculateTime();
        total.setValue(total.doubleValue() + diff);
    }

    private Interval retrieveCurrent() {
        ObservableList<Node> children = entriesTable.getChildren();
        int lastPos = children.size() - 1;
        return (Interval) children.get(lastPos);
    }

    private void createTimeRow() {
        Interval interval = new Interval();
        entriesTable.getChildren().add(interval);
    }
}
