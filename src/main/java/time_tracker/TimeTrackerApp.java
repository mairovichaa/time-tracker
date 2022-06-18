package time_tracker;

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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import time_tracker.component.Interval;
import time_tracker.component.stopwatch.StopWatchTab;
import time_tracker.config.StopwatchConfiguration;
import time_tracker.service.StopwatchRecordService;

import java.awt.*;
import java.util.stream.Collectors;

public class TimeTrackerApp extends Application {

    private VBox entriesTable;

    private Button addBtn;

    private DoubleProperty total = new SimpleDoubleProperty(0);
    private Text totalText = new Text();

    private ClipboardContent clipboardContent = new ClipboardContent();
    private Clipboard clipboard = Clipboard.getSystemClipboard();

    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();
        Tab tab = new Tab();
        tab.setText("Interval counter");

        var stopwatchConfiguration = new StopwatchConfiguration();
        var stopwatchRecordService = stopwatchConfiguration.stopwatchRecordService();
        var stopWatchTab = stopwatchConfiguration.stopWatchTab(stopwatchRecordService);
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
            System.out.println(stringForCopy);
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
        Scene scene = new Scene(tabPane, 300, 400);

        primaryStage.setScene(scene);
        primaryStage.show();
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
