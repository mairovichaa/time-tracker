package time_tracker.component.stopwatch;

import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.NonNull;
import time_tracker.Utils;
import time_tracker.model.StopwatchRecordMeasurement;

import java.io.IOException;

import static time_tracker.Constants.DATA_TIME_FORMATTER;
import static time_tracker.component.Utils.load;

public class MeasurementVBox extends VBox {

    @FXML
    private Label startedAt;
    @FXML
    private Label finishedAt;
    @FXML
    private Label total;
    @FXML
    private Label comment;

    public MeasurementVBox(@NonNull final StopwatchRecordMeasurement measurement) {
        load("/fxml/stopwatch/MeasurementVBox.fxml", this);

        startedAt.textProperty()
                .bind(new StringBinding() {
                    {super.bind(measurement.getStartedAtProperty());}
                    @Override
                    protected String computeValue() {
                        var startedAt = measurement.getStartedAtProperty().getValue();
                        return DATA_TIME_FORMATTER.format(startedAt);
                    }
                });

        finishedAt.textProperty()
                .bind(new StringBinding() {
                    {super.bind(measurement.getStoppedAtProperty());}
                    @Override
                    protected String computeValue() {
                        var stoppedAt = measurement.getStoppedAtProperty().getValue();
                        return DATA_TIME_FORMATTER.format(stoppedAt);
                    }
                });
        total.textProperty()
                .bind(new StringBinding() {
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
                        var comment = measurement.getNoteProperty().getValue();
                        if (comment.isEmpty()){
                            return "No comment";
                        }
                        return comment;
                    }
                });
    }
}
