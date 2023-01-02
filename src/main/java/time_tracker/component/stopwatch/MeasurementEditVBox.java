package time_tracker.component.stopwatch;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.model.StopwatchRecordMeasurement;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;

import static time_tracker.Constants.DATA_TIME_FORMATTER_2;
import static time_tracker.component.Utils.load;

@Log
public class MeasurementEditVBox extends VBox {

    private static final String LOCAL_TIME_FORMATTER_PATTERN = "HH:mm:ss";
    private static final DateTimeFormatter LOCAL_TIME_FORMATTER = DateTimeFormatter.ofPattern(LOCAL_TIME_FORMATTER_PATTERN);
    @FXML
    private MFXTextField startedAt;
    @FXML
    private MFXTextField finishedAt;
    @FXML
    private Label total;
    @FXML
    private MFXTextField comment;
    @FXML
    private Button save;
    @FXML
    private Button cancel;

    private final AtomicReference<LocalTime> startedAtResult = new AtomicReference<>();
    private final AtomicReference<LocalTime> finishedAtResult = new AtomicReference<>();

    public MeasurementEditVBox(
            @NonNull final StopwatchRecordMeasurement measurement,
            @NonNull final Stage stage
    ) {
        load("/fxml/stopwatch/MeasurementEditVBox.fxml", this);

        var startedAtValue = measurement.getStartedAtProperty().getValue();
        var startedAtFormatted = DATA_TIME_FORMATTER_2.format(startedAtValue);
        this.startedAt.textProperty().setValue(startedAtFormatted);
        startedAtResult.set(startedAtValue);

        var stoppedAtValue = measurement.getStoppedAtProperty().getValue();
        var stoppedAtFormatted = DATA_TIME_FORMATTER_2.format(stoppedAtValue);
        this.finishedAt.textProperty().setValue(stoppedAtFormatted);
        finishedAtResult.set(stoppedAtValue);

        var noteValue = measurement.getNoteProperty().getValue();
        this.comment.textProperty().setValue(noteValue);


        total.textProperty()
                .bind(new StringBinding() {
                    {
                        bind(startedAt.textProperty(), finishedAt.textProperty());
                    }

                    @Override
                    protected String computeValue() {
                        log.fine(() -> "Change total value");
                        var startedAtText = startedAt.getText();
                        LocalTime startedAtTime;
                        try {
                            startedAtTime = LocalTime.parse(startedAtText, LOCAL_TIME_FORMATTER);
                        } catch (Exception exception) {
                            log.fine(() -> "Can't parse startedAt: " + exception.getMessage());
                            return "startedAt has wrong format: " + LOCAL_TIME_FORMATTER_PATTERN;
                        }
                        startedAtResult.set(startedAtTime);

                        var finishedAtText = finishedAt.getText();
                        LocalTime finishedAtTime;
                        try {
                            finishedAtTime = LocalTime.parse(finishedAtText, LOCAL_TIME_FORMATTER);
                        } catch (Exception exception) {
                            log.fine(() -> "Can't parse finishedAt: " + exception.getMessage());
                            return "finishedAt has wrong format: " + LOCAL_TIME_FORMATTER_PATTERN;
                        }
                        finishedAtResult.set(finishedAtTime);

                        var duration = Duration.between(startedAtTime, finishedAtTime);
                        return Utils.formatDuration(duration);
                    }
                });

        save.setOnMouseClicked(e -> {
            measurement.setStartedAt(startedAtResult.get());
            measurement.setStoppedAt(finishedAtResult.get());
            measurement.getNoteProperty().set(comment.getText());
            stage.close();
        });

        cancel.setOnMouseClicked(e -> stage.close());
    }
}