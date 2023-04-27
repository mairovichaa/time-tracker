package time_tracker.component.stopwatch.record;

import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.extern.java.Log;
import time_tracker.TimeTrackerApp;
import time_tracker.Utils;
import time_tracker.component.stopwatch.measurement.MeasurementInProgressVBox;
import time_tracker.config.GlobalContext;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;
import time_tracker.service.StopwatchRecordService;

import java.util.List;
import java.util.logging.Level;

import static time_tracker.component.Utils.load;

@Log
public class StopwatchRecordVBox extends VBox {

    @FXML
    private Label recordIdLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label totalTimeLabel;
    @FXML
    private Label deleteLabel;
    @FXML
    private Label amountOfMeasurements;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private MFXToggleButton trackedToggle;
    @FXML
    private VBox inProgressMeasurementVBox;

    private final StopwatchRecord stopwatchRecord;
    private final StopwatchRecordService stopwatchRecordService;
    private final StopwatchRecord record;

    public StopwatchRecordVBox(
            @NonNull final StopwatchRecord stopwatchRecord
    ) {
        load("/fxml/stopwatch/record/StopwatchRecordVBox.fxml", this);
        this.record = stopwatchRecord;

        nameLabel.textProperty().bind(stopwatchRecord.getNameProperty());

        startButton.disableProperty()
                .bind(stopwatchRecord.getHasMeasurementInProgressProperty());
        stopButton.disableProperty()
                .bind(Bindings.not(stopwatchRecord.getHasMeasurementInProgressProperty()));

        trackedToggle.selectedProperty()
                .bindBidirectional(stopwatchRecord.getTrackedProperty());


        this.stopwatchRecord = stopwatchRecord;
        this.stopwatchRecordService = GlobalContext.get(StopwatchRecordService.class);

        stopwatchRecord.getTrackedProperty()
                .addListener((observable, oldValue, newValue) -> stopwatchRecordService.store());

        var appProperties = GlobalContext.get(StopwatchProperties.class);
        var isDevMode = appProperties.isDevMode();
        recordIdLabel.setVisible(isDevMode);
        recordIdLabel.setText(Long.toString(stopwatchRecord.getId()));

        bindTotalTime();
        bindAmountOfMeasurements();
        bindMeasurementInProgress();

        this.setOnMouseClicked(ignored -> chosen());

        var stopWatchAppState = GlobalContext.get(StopWatchAppState.class);
        stopWatchAppState.getChosenStopwatchRecord()
                .addListener(c -> {
                    log.fine(() -> "chosen stopwatch record have been changed");
                    var chosenStopwatchRecord = stopWatchAppState.getChosenStopwatchRecord().get();
                    if (stopwatchRecord.equals(chosenStopwatchRecord)) {
                        this.getStyleClass()
                                .add("record-chosen");
                    } else {
                        this.getStyleClass()
                                .remove("record-chosen");
                    }
                });
    }

    private void bindAmountOfMeasurements() {
        var stopwatchRecords = stopwatchRecord.getMeasurementsProperty();
        stopwatchRecords.addListener((ListChangeListener<StopwatchRecordMeasurement>) c -> {
            log.fine(() -> "stopwatch records have been changed ");
            log.finest(() -> "actual " + c);
            setAmountOfMeasurements(c.getList());
        });
        setAmountOfMeasurements(stopwatchRecords);
    }

    private void setAmountOfMeasurements(List<? extends StopwatchRecordMeasurement> measurements) {
        var amountOfMeasurements = measurements.size();
        this.amountOfMeasurements.setText(Integer.toString(amountOfMeasurements));
    }

    @FXML
    protected void start() {
        log.log(Level.FINE, "stopwatchStartButton is clicked");
        stopwatchRecordService.startNewMeasurement(stopwatchRecord);
    }

    @FXML
    protected void stop() {
        log.log(Level.FINE, "stopwatchStopButton is clicked");
        stopwatchRecordService.stopMeasurement(stopwatchRecord);
        stopwatchRecordService.store();
    }

    @FXML
    protected void delete(MouseEvent event) {
        log.log(Level.FINE, "deleteButton is clicked");
        stopwatchRecordService.delete(stopwatchRecord);
        stopwatchRecordService.store();
        event.consume();
    }

    private void chosen() {
        log.log(Level.FINE, "stopwatchChosenButton is clicked");

        var stopWatchAppState = GlobalContext.get(StopWatchAppState.class);
        stopWatchAppState.getChosenStopwatchRecord().set(stopwatchRecord);
    }


    private void bindTotalTime() {
        var measurementsTotalInSecsLongBinding = stopwatchRecord.getMeasurementsTotalInSecsLongBinding();

        StringBinding longBinding = new StringBinding() {
            {
                super.bind(measurementsTotalInSecsLongBinding);
            }

            @Override
            protected String computeValue() {
                var totalInSecs = measurementsTotalInSecsLongBinding.get();
                return Utils.formatDuration(totalInSecs);
            }
        };
        totalTimeLabel.textProperty()
                .bind(longBinding);
    }

    private void bindMeasurementInProgress() {
        stopwatchRecord.getMeasurementInProgressProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        inProgressMeasurementVBox.getChildren().clear();
                        return;
                    }
                    var hBox = new MeasurementInProgressVBox(newValue);
                    inProgressMeasurementVBox.getChildren().add(hBox);
                });
    }

    @FXML
    protected void rename() {
        log.log(Level.FINE, "'Rename' button is clicked");

        var dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(TimeTrackerApp.primaryStage);
        var dialogVbox = new RecordRenameVBox(record, dialog);
        var dialogScene = new Scene(dialogVbox, 200, 80);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    @FXML
    protected void move() {
        log.log(Level.FINE, "'Move' button is clicked");
        var dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(TimeTrackerApp.primaryStage);
        var dialogVbox = new RecordMoveVBox(record, dialog);
        var dialogScene = new Scene(dialogVbox, 200, 150);
        dialog.setScene(dialogScene);
        dialog.show();
    }
}
