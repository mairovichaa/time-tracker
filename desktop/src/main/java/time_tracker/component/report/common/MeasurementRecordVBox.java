package time_tracker.component.report.common;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import time_tracker.Utils;
import time_tracker.common.GlobalContext;
import time_tracker.common.annotation.NonNull;
import time_tracker.model.ReportState;
import time_tracker.model.StopwatchRecordMeasurement;

import static time_tracker.Constants.DATA_TIME_FORMATTER;
import static time_tracker.component.Utils.load;

public class MeasurementRecordVBox extends VBox {

    private final static String DURATION_FORMAT = "%s -> %s = %s";

    @FXML
    private Label fromToAndTotalDurationLabel;
    @FXML
    private Label noteLabel;

    public MeasurementRecordVBox(@NonNull final StopwatchRecordMeasurement measurement) {
        load("/fxml/report/common/MeasurementRecordVBox.fxml", this);

        String fromToAndTotalTime = String.format(DURATION_FORMAT, DATA_TIME_FORMATTER.format(measurement.getStartedAt()), DATA_TIME_FORMATTER.format(measurement.getStoppedAt()), Utils.formatDuration(measurement.getDuration())
        );
        fromToAndTotalDurationLabel.setText(fromToAndTotalTime);


        String note = measurement.getNote().isEmpty() ? "-" : measurement.getNote();
        noteLabel.setText(note);

        var reportState = GlobalContext.get(ReportState.class);
        reportState.getShowTimeProperty().addListener((observable, oldValue, newIsShowTime) -> {
            if (!newIsShowTime && measurement.getNote().isEmpty()) {
                this.setManaged(false);
                this.setVisible(false);
                return;
            } else {
                this.setManaged(true);
                this.setVisible(true);
            }

            fromToAndTotalDurationLabel.managedProperty().set(newIsShowTime);
            fromToAndTotalDurationLabel.visibleProperty().set(newIsShowTime);
        });
    }
}