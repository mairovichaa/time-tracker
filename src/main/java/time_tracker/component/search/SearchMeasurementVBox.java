package time_tracker.component.search;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import time_tracker.Utils;
import time_tracker.annotation.NonNull;
import time_tracker.model.StopwatchRecordMeasurement;

import java.time.Duration;
import java.time.LocalTime;

import static javafx.beans.binding.Bindings.createStringBinding;
import static time_tracker.Constants.DATA_TIME_FORMATTER;
import static time_tracker.component.Utils.load;

public class SearchMeasurementVBox extends VBox {

    @FXML
    private Label startedAt;
    @FXML
    private Label finishedAt;
    @FXML
    private Label total;
    @FXML
    private Label comment;

    public SearchMeasurementVBox(@NonNull final StopwatchRecordMeasurement measurement) {
        load("/fxml/search/SearchMeasurementVBox.fxml", this);

        startedAt.textProperty()
                .bind(createStringBinding(
                        () -> {
                            LocalTime startedAt = measurement.getStartedAt();
                            return DATA_TIME_FORMATTER.format(startedAt);
                        }, measurement.getStartedAtProperty()));

        finishedAt.textProperty()
                .bind(createStringBinding(
                        () -> {
                            LocalTime stoppedAt = measurement.getStoppedAt();
                            return DATA_TIME_FORMATTER.format(stoppedAt);
                        }, measurement.getStoppedAtProperty()));

        total.textProperty()
                .bind(createStringBinding(
                        () -> {
                            Duration duration = measurement.getDuration();
                            return Utils.formatDuration(duration);
                        }, measurement.getDurationProperty()));

        comment.textProperty()
                .bind(createStringBinding(
                        () -> {
                            String comment = measurement.getNote();
                            if (comment.isEmpty()) {
                                return "No comment";
                            }
                            return comment;
                        }, measurement.getNoteProperty()));
    }
}
