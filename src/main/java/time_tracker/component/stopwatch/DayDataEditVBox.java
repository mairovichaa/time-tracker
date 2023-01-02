package time_tracker.component.stopwatch;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.model.DayData;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static time_tracker.component.Utils.load;

@Log
public class DayDataEditVBox extends VBox {
    private static final String LOCAL_TIME_FORMATTER_PATTERN = "HH:mm:ss";
    private static final DateTimeFormatter LOCAL_TIME_FORMATTER = DateTimeFormatter.ofPattern(LOCAL_TIME_FORMATTER_PATTERN);
    @FXML
    private MFXTextField expectedTotalField;
    @FXML
    private MFXTextField commentField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    public DayDataEditVBox(
            @NonNull final DayData dayData,
            @NonNull final Stage stage
    ) {
        load("/fxml/stopwatch/DayDataEditVBox.fxml", this);

        var expectedTotalInSecs = dayData.getExpectedTotalInSecs().getValue();
        var duration = Utils.formatDuration(expectedTotalInSecs);
        expectedTotalField.textProperty().setValue(duration);

        var noteValue = dayData.getNoteProperty().getValue();
        commentField.textProperty().setValue(noteValue);

        saveButton.setOnMouseClicked(e -> {
            var newExpectedTotalInSecs = expectedTotalField.textProperty().get();
            var newExpectedLocalTime = LocalTime.parse(newExpectedTotalInSecs, LOCAL_TIME_FORMATTER);

            dayData.getExpectedTotalInSecs().setValue(newExpectedLocalTime.toSecondOfDay());
            dayData.getNoteProperty().set(commentField.getText());
            stage.close();
        });

        cancelButton.setOnMouseClicked(e -> stage.close());
    }
}