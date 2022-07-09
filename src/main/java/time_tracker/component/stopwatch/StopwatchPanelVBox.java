package time_tracker.component.stopwatch;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import time_tracker.model.StopwatchRecord;
import time_tracker.service.StopwatchRecordService;
import time_tracker.service.dev.RandomStopwatchRecordFactory;

@RequiredArgsConstructor
public class StopwatchPanelVBox extends VBox {

    // TODO move to configs
    private final boolean devMode = true;

    @NonNull
    private final ObservableList<StopwatchRecord> stopwatchRecords;
    @NonNull
    private final StopwatchRecordService stopwatchRecordService;
    @NonNull
    private final Button printButton = new Button("Print");
    @NonNull
    private final Button generateRandomButton = new Button("Generate random");
    @NonNull
    private final Button addStopwatchButton = new Button("Add");
    @NonNull
    private final TextField stopwatchNameTextField = new TextField();
    private final HBox createStopwatchWrapper = new HBox(stopwatchNameTextField, addStopwatchButton);

    public StopwatchPanelVBox(
            @NonNull final StopwatchRecordService stopwatchRecordService,
            @NonNull final RandomStopwatchRecordFactory randomStopwatchRecordFactory
    ) {
//        TODO it has too small space - it's not possible to see comment
        this.stopwatchRecordService = stopwatchRecordService;
        this.stopwatchRecords = stopwatchRecordService.findAll();

        this.stopwatchRecords.addListener((ListChangeListener<StopwatchRecord>) c -> {
            System.out.println("StopWatchTab: stopwatchRecords's listener");
            redrawList();
        });

        printButton.setOnMouseClicked(e -> stopwatchRecordService.store());
        generateRandomButton.setOnMouseClicked(e -> randomStopwatchRecordFactory.create());

        addStopwatchButton.setOnMouseClicked(e -> {
            System.out.println("addStopwatchButton is clicked");
            var stopwatchName = stopwatchNameTextField.getText();
            stopwatchRecordService.create(stopwatchName);
            stopwatchNameTextField.clear();
        });
        redrawList();
    }

    private void redrawList() {
        var children = this.getChildren();
        children.clear();

        stopwatchRecords.stream()
                // TODO introduce a factory for it
                .map(it -> new StopwatchRecordVBox(it, stopwatchRecordService))
                .forEach(children::add);
        children.addAll(printButton, createStopwatchWrapper);
        if (devMode) {
            children.addAll(generateRandomButton);
        }
    }

}
