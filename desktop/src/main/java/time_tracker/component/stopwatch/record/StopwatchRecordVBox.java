package time_tracker.component.stopwatch.record;

import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.NonNull;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.common.GlobalContext;
import time_tracker.component.common.DialogFactory;
import time_tracker.component.common.Icon;
import time_tracker.component.stopwatch.measurement.MeasurementInProgressVBox;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;
import time_tracker.service.AppStateService;
import time_tracker.service.StopwatchRecordService;

import java.util.List;
import java.util.logging.Level;

import static time_tracker.component.Utils.load;
import static time_tracker.component.common.Confirmation.requireConfirmation;
import static time_tracker.component.common.IconButton.initIconButton;

@Log
public class StopwatchRecordVBox extends Pane {

    @FXML
    private VBox recordVBox;
    @FXML
    private Label recordIdLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label totalTimeIconLabel;
    @FXML
    private Label totalTimeLabel;
    @FXML
    private Button deleteButton;
    @FXML
    private Label amountOfMeasurements;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button editNameButton;
    @FXML
    private Button moveButton;
    @FXML
    private VBox inProgressMeasurementVBox;
    @FXML
    private VBox measurementInProgressVBoxWrapper;

    @FXML
    private Button trackButton;
    @FXML
    private Button notTrackButton;

    private final AppStateService appStateService;

    private final StopwatchRecord stopwatchRecord;
    private final StopwatchRecordService stopwatchRecordService;
    private final StopwatchRecord record;
    // reference has to be stored as WeakListChangeListener is used to register it
    private final InvalidationListener chosenStopwatchRecordListener;

    public StopwatchRecordVBox(
            @NonNull final StopwatchRecord stopwatchRecord
    ) {
        load("/fxml/stopwatch/record/StopwatchRecordVBox.fxml", this);
        this.record = stopwatchRecord;

        nameLabel.textProperty().bind(stopwatchRecord.getNameProperty());

        initIconButton(startButton, 30, Icon.STOPWATCH);
        initIconButton(stopButton, 30, Icon.STOPWATCH, List.of("icon-button", "icon-button-green"), List.of("button-icon-green"));

        initIconButton(deleteButton, 15, Icon.DELETE);
        initIconButton(moveButton, 15, Icon.CALENDAR);
        initIconButton(editNameButton, 15, Icon.PEN);

        stopButton.disableProperty()
                .bind(Bindings.not(stopwatchRecord.getHasMeasurementInProgressProperty()));

        initIconButton(trackButton, 20, Icon.CHECK);
        trackButton.visibleProperty().bind(Bindings.not(stopwatchRecord.getTrackedProperty()));
        trackButton.managedProperty().bind(Bindings.not(stopwatchRecord.getTrackedProperty()));

        initIconButton(notTrackButton, 20, Icon.CHECK, List.of("icon-button", "icon-button-green"), List.of("button-icon-green"));
        notTrackButton.visibleProperty().bind(stopwatchRecord.getTrackedProperty());
        notTrackButton.managedProperty().bind(stopwatchRecord.getTrackedProperty());

        this.stopwatchRecord = stopwatchRecord;
        this.stopwatchRecordService = GlobalContext.get(StopwatchRecordService.class);
        this.appStateService = GlobalContext.get(AppStateService.class);

        // TODO it seems that listener has to be removed or to be moved to another place
        stopwatchRecord.getTrackedProperty()
                .addListener((observable, oldValue, newValue) -> stopwatchRecordService.store());

        var appProperties = GlobalContext.get(StopwatchProperties.class);
        var isDevMode = appProperties.isDevMode();
        recordIdLabel.setVisible(isDevMode);
        recordIdLabel.setManaged(isDevMode);
        recordIdLabel.setText("#" + stopwatchRecord.getId());

        bindTotalTime();
        bindAmountOfMeasurements();
        bindMeasurementInProgress();

        this.setOnMouseClicked(ignored -> chosen());

        var stopWatchAppState = GlobalContext.get(StopWatchAppState.class);
        this.chosenStopwatchRecordListener = c -> {
            log.fine(() -> "chosen stopwatch record have been changed");
            var chosenStopwatchRecord = stopWatchAppState.getChosenStopwatchRecord().get();
            if (stopwatchRecord.equals(chosenStopwatchRecord)) {
                recordVBox.getStyleClass()
                        .add("record-chosen");
            } else {
                recordVBox.getStyleClass()
                        .remove("record-chosen");
            }
        };
        stopWatchAppState.getChosenStopwatchRecord()
                .addListener(new WeakInvalidationListener(chosenStopwatchRecordListener));

        editNameButton.setOnMouseClicked(e -> rename());
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

        requireConfirmation().whenComplete((it, ex) -> {
            if (it) {
                appStateService.delete(stopwatchRecord);
            } else {
                log.fine(() -> "'No' button is clicked");
            }
        });

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
                        measurementInProgressVBoxWrapper.setVisible(false);
                        measurementInProgressVBoxWrapper.setManaged(false);
                        stopButton.setVisible(false);
                        stopButton.setManaged(false);
                        startButton.setManaged(true);
                        startButton.setVisible(true);
                        return;
                    }
                    var hBox = new MeasurementInProgressVBox(newValue);
                    inProgressMeasurementVBox.getChildren().add(hBox);
                    measurementInProgressVBoxWrapper.setVisible(true);
                    measurementInProgressVBoxWrapper.setManaged(true);
                    startButton.setManaged(false);
                    startButton.setVisible(false);
                    stopButton.setVisible(true);
                    stopButton.setManaged(true);
                });
    }

    @FXML
    protected void rename() {
        log.log(Level.FINE, "'Rename' button is clicked");
        DialogFactory.createAndShow(
                stage -> new RecordRenameVBox(record, stage),
                "Rename record"
        );
    }

    @FXML
    protected void move() {
        log.log(Level.FINE, "'Move' button is clicked");
        DialogFactory.createAndShow(
                stage -> new RecordMoveVBox(record, stage),
                "Change date"
        );
    }

    @FXML
    protected void track() {
        log.log(Level.FINE, "'Track' button is clicked");
        record.setTracked(true);
    }

    @FXML
    protected void notTrack() {
        log.log(Level.FINE, "'Not track' button is clicked");
        record.setTracked(false);
    }
}
