package time_tracker.component.stopwatch;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.NonNull;
import time_tracker.Utils;
import time_tracker.model.StopwatchRecordMeasurement;

import static time_tracker.Constants.DATA_TIME_FORMATTER;
import static time_tracker.component.Utils.load;

public class MeasurementInProgressVBox extends VBox {

    @FXML
    private Label startedAt;
    @FXML
    private Label finishedAt;
    @FXML
    private Label total;
    @FXML
    private Label comment;
    @FXML
    private MFXTextField commentInput;

    public MeasurementInProgressVBox(@NonNull final StopwatchRecordMeasurement measurement) {
        load("/fxml/stopwatch/MeasurementInProgressVBox.fxml", this);

        startedAt.textProperty()
                .bind(new StringBinding() {
                    {super.bind(measurement.getStartedAtProperty());}
                    @Override
                    protected String computeValue() {
                        var startedAt = measurement.getStartedAt();
                        return DATA_TIME_FORMATTER.format(startedAt);
                    }
                });

        finishedAt.textProperty()
                .bind(new StringBinding() {
                    {super.bind(measurement.getStoppedAtProperty());}
                    @Override
                    protected String computeValue() {
                        var stoppedAt = measurement.getStoppedAt();
                        return DATA_TIME_FORMATTER.format(stoppedAt);
                    }
                });

        total.textProperty()
                .bind(new StringBinding() {
                    {super.bind(measurement.getDurationProperty());}
                    @Override
                    protected String computeValue() {
                        var duration = measurement.getDuration();
                        return Utils.formatDuration(duration);
                    }
                });

        commentInput.textProperty()
                .bindBidirectional(measurement.getNoteProperty());

        comment.textProperty()
                .bind(new StringBinding() {
                    {
                        bind(measurement.getNoteProperty());
                    }
                    @Override
                    protected String computeValue() {
                        var comment = measurement.getNote();
                        if (comment.isEmpty()){
                            return "No comment";
                        }
                        return comment;
                    }
                });

    }

}
