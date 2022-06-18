package time_tracker.component.stopwatch;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.NonNull;
import time_tracker.model.StopwatchRecord;
import time_tracker.service.StopwatchRecordService;

public class StopWatchTab extends Tab {

    @NonNull
    private final ObservableList<StopwatchRecord> stopwatchRecords;
    @NonNull
    private final StopwatchRecordService stopwatchRecordService;
    @NonNull
    private final ObservableList<StopwatchRecordVBox> records = FXCollections.observableArrayList();
    public StopWatchTab(@NonNull final StopwatchRecordService stopwatchRecordService) {
        super("Stopwatch");

        this.stopwatchRecordService = stopwatchRecordService;
        this.stopwatchRecords = stopwatchRecordService.findAll();

        var stopwatchNameTextField = new TextField();
        var addStopwatchButton = new Button("Add");
        var createStopwatchWrapper = new HBox(stopwatchNameTextField, addStopwatchButton);
        var printButton = new Button("Print");

        printButton.setOnMouseClicked(e -> {
            for (StopwatchRecordVBox record : records) {
                System.out.println(record.getName());
                for (StopwatchRecordMeasurementHBox measurement : record.getMeasurements()) {
                    System.out.println(measurement.getMeasurementString());
                }
                System.out.println("---");
            }
        });

        VBox contentWrapper = new VBox(printButton, createStopwatchWrapper);

        addStopwatchButton.setOnMouseClicked(e -> {
            System.out.println("addStopwatchButton is clicked");
            var stopwatchName = stopwatchNameTextField.getText();
            var stopwatchRecord = stopwatchRecordService.create(stopwatchName);

            var stopwatchRecordVBox = new StopwatchRecordVBox(stopwatchName);
            records.add(stopwatchRecordVBox);

            var children = contentWrapper.getChildren();
            children.add(children.size() - 1, stopwatchRecordVBox);

            stopwatchNameTextField.clear();
        });

        var scrollPane = new ScrollPane(contentWrapper);
        this.setContent(scrollPane);
    }

}
