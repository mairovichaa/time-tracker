package time_tracker.component.stopwatch.measurement;

import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import lombok.NonNull;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.component.common.DialogFactory;
import time_tracker.component.common.Icon;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopwatchRecordMeasurement;
import time_tracker.service.StopwatchMeasurementService;
import time_tracker.service.StopwatchRecordService;

import static time_tracker.Constants.DATA_TIME_FORMATTER;
import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;
import static time_tracker.component.common.Confirmation.requireConfirmation;
import static time_tracker.component.common.IconUtils.initIconLabeled;

@Log
public class MeasurementPane extends Pane {

    @FXML
    private Label measurementIdLabel;
    @FXML
    private HBox measurementIdWrapper;
    @FXML
    private Label startedAt;
    @FXML
    private Label finishedAt;
    @FXML
    private Label total;
    @FXML
    private Label comment;
    @FXML
    private Button moveButton;
    @FXML
    private Button editNameButton;
    @FXML
    private Button deleteButton;

    private final StopwatchRecordMeasurement measurement;
    private final StopwatchRecordService stopwatchRecordService;
    private final StopwatchMeasurementService stopwatchMeasurementService;

    public MeasurementPane(@NonNull final StopwatchRecordMeasurement measurement) {
        load("/fxml/stopwatch/measurement/MeasurementPane.fxml", this);

        this.measurement = measurement;

        var appProperties = CONTEXT.get(StopwatchProperties.class);
        var isDevMode = appProperties.isDevMode();
        measurementIdWrapper.setVisible(isDevMode);
        measurementIdWrapper.setManaged(isDevMode);
        measurementIdLabel.setText(Long.toString(measurement.getId()));

        this.stopwatchRecordService = CONTEXT.get(StopwatchRecordService.class);
        this.stopwatchMeasurementService = CONTEXT.get(StopwatchMeasurementService.class);

        startedAt.textProperty()
                .bind(new StringBinding() {
                    {
                        super.bind(measurement.getStartedAtProperty());
                    }

                    @Override
                    protected String computeValue() {
                        var startedAt = measurement.getStartedAt();
                        return DATA_TIME_FORMATTER.format(startedAt);
                    }
                });

        finishedAt.textProperty()
                .bind(new StringBinding() {
                    {
                        super.bind(measurement.getStoppedAtProperty());
                    }

                    @Override
                    protected String computeValue() {
                        var stoppedAt = measurement.getStoppedAt();
                        return DATA_TIME_FORMATTER.format(stoppedAt);
                    }
                });
        total.textProperty()
                .bind(new StringBinding() {
                    {
                        super.bind(measurement.getDurationProperty());
                    }

                    @Override
                    protected String computeValue() {
                        var duration = measurement.getDuration();
                        return Utils.formatDuration(duration);
                    }
                });


        comment.textProperty()
                .bind(new StringBinding() {
                    {
                        bind(measurement.getNoteProperty());
                    }

                    @Override
                    protected String computeValue() {
                        var comment = measurement.getNote();
                        if (comment.isEmpty()) {
                            return "No comment";
                        }
                        return comment;
                    }
                });

        initIconLabeled(deleteButton, 15, Icon.DELETE);
        initIconLabeled(moveButton, 15, Icon.MOVE_GROUP);
        initIconLabeled(editNameButton, 15, Icon.PEN);
    }

    @FXML
    protected void edit() {
        log.fine("Edit button is clicked for measurement = " + measurement.getId());
        DialogFactory.createAndShow(
                stage -> new MeasurementEditVBox(measurement, stage),
                "Edit measurement"
        );
    }

    @FXML
    protected void move() {
        log.fine("'Move' button is clicked for measurement = " + measurement.getId());
        DialogFactory.createAndShow(
                stage -> new MeasurementMoveVBox(measurement, stage),
                "Choose new record"
        );
    }

    @FXML
    protected void delete() {
        log.fine("Delete button is clicked for measurement = " + measurement.getId());

        requireConfirmation().whenComplete((it, ex) -> {
            if (it) {
                stopwatchMeasurementService.delete(measurement.getId());
                stopwatchRecordService.store();
            } else {
                log.fine(() -> "'No' button is clicked");
            }
        });
    }
}
