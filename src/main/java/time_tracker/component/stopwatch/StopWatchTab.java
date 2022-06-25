package time_tracker.component.stopwatch;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.NonNull;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;
import time_tracker.service.StopwatchRecordService;

public class StopWatchTab extends Tab {

    @NonNull
    private final ObservableList<StopwatchRecord> stopwatchRecords;
    @NonNull
    private final StopwatchRecordService stopwatchRecordService;
    @NonNull
    private final ObservableList<StopwatchRecordVBox> records = FXCollections.observableArrayList();
    @NonNull
    private final VBox contentWrapper;
    @NonNull
    private final Button printButton = new Button("Print");
    @NonNull
    private final Button addStopwatchButton = new Button("Add");
    @NonNull
    private final TextField stopwatchNameTextField = new TextField();
    private final HBox createStopwatchWrapper = new HBox(stopwatchNameTextField, addStopwatchButton);

    public StopWatchTab(@NonNull final StopwatchRecordService stopwatchRecordService) {
        super("Stopwatch");

        this.stopwatchRecordService = stopwatchRecordService;
        this.stopwatchRecords = stopwatchRecordService.findAll();

        this.stopwatchRecords.addListener((ListChangeListener<StopwatchRecord>) c -> {
            System.out.println("StopWatchTab: stopwatchRecords's listener");
            redrawList();
        });

        printButton.setOnMouseClicked(e -> {
            for (StopwatchRecord record : stopwatchRecords) {
                System.out.println(record.getName());
                for (StopwatchRecordMeasurement measurement : record.getMeasurementsProperty()) {
                    System.out.println(measurement.getStopwatchStringProperty().get());
                }
                System.out.println("---");
            }
        });

        addStopwatchButton.setOnMouseClicked(e -> {
            System.out.println("addStopwatchButton is clicked");
            var stopwatchName = stopwatchNameTextField.getText();
            stopwatchRecordService.create(stopwatchName);
            stopwatchNameTextField.clear();
        });

        contentWrapper = new VBox();
        var scrollPane = new ScrollPane(contentWrapper);
        this.setContent(scrollPane);
        redrawList();
    }

    private void redrawList() {
        var children = contentWrapper.getChildren();
        children.clear();

        stopwatchRecords.stream()
                // TODO introduce a factory for it
                .map(it -> new StopwatchRecordVBox(it, stopwatchRecordService))
                .forEach(children::add);

        children.addAll(printButton, createStopwatchWrapper);
    }

}
