package time_tracker.component.report.common;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import time_tracker.Utils;
import time_tracker.common.annotation.NonNull;
import time_tracker.model.ReportState;
import time_tracker.model.StopwatchRecordMeasurement;

import static time_tracker.Constants.DATA_TIME_FORMATTER;
import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;

public class MeasurementRecordVBox extends VBox {

    @FXML
    private HBox fromToAndTotalDurationHBox;

    @FXML
    private Label startedAt;

    @FXML
    private Label finishedAt;

    @FXML
    private Label total;
    @FXML
    private Label noteLabel;

    public MeasurementRecordVBox(@NonNull final StopwatchRecordMeasurement measurement) {
        load("/fxml/report/common/MeasurementRecordVBox.fxml", this);

        startedAt.setText(DATA_TIME_FORMATTER.format(measurement.getStartedAt()));
        finishedAt.setText(DATA_TIME_FORMATTER.format(measurement.getStoppedAt()));
        total.setText(Utils.formatDuration(measurement.getDuration()));

        String note = measurement.getNote().isEmpty() ? "-" : measurement.getNote();
        noteLabel.setText(note);

        var reportState = CONTEXT.get(ReportState.class);
        reportState.getShowTimeProperty().addListener((observable, oldValue, newIsShowTime) -> {
            if (!newIsShowTime && measurement.getNote().isEmpty()) {
                this.setManaged(false);
                this.setVisible(false);
                return;
            } else {
                this.setManaged(true);
                this.setVisible(true);
            }

            fromToAndTotalDurationHBox.managedProperty().set(newIsShowTime);
            fromToAndTotalDurationHBox.visibleProperty().set(newIsShowTime);
        });
    }
}