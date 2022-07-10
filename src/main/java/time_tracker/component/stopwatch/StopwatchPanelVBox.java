package time_tracker.component.stopwatch;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopwatchRecord;
import time_tracker.service.StopwatchRecordService;
import time_tracker.service.dev.RandomStopwatchRecordFactory;

@Log
@RequiredArgsConstructor
public class StopwatchPanelVBox extends VBox {

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
    @NonNull
    private final StopwatchProperties stopwatchProperties;

    public StopwatchPanelVBox(
            @NonNull final StopwatchRecordService stopwatchRecordService,
            @NonNull final RandomStopwatchRecordFactory randomStopwatchRecordFactory,
            @NonNull final StopwatchProperties stopwatchProperties
    ) {
//        TODO it has too small space - it's not possible to see comment
        this.stopwatchRecordService = stopwatchRecordService;
        this.stopwatchRecords = stopwatchRecordService.findAll();
        this.stopwatchProperties = stopwatchProperties;

        this.stopwatchRecords.addListener((ListChangeListener<StopwatchRecord>) c -> {
            log.fine(() -> "stopwatch records have been changed");
            redrawList();
        });

        printButton.setOnMouseClicked(e -> stopwatchRecordService.store());
        generateRandomButton.setOnMouseClicked(e -> randomStopwatchRecordFactory.create());

        addStopwatchButton.setOnMouseClicked(e -> {
            log.fine(() -> "addStopwatchButton is clicked");
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
        if (stopwatchProperties.isDevMode()) {
            children.addAll(generateRandomButton);
        }
    }

}
