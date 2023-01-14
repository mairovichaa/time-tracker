package time_tracker.component.stopwatch;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.config.GlobalContext;
import time_tracker.model.DayData;
import time_tracker.service.DayStatisticsService;
import time_tracker.service.StopwatchRecordService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static time_tracker.component.Utils.load;

@Log
public class DayDataEditVBox extends VBox {
    private static final String LOCAL_TIME_FORMATTER_PATTERN = "HH:mm:ss";
    private static final DateTimeFormatter LOCAL_TIME_FORMATTER = DateTimeFormatter.ofPattern(LOCAL_TIME_FORMATTER_PATTERN);
    @FXML
    private Label dateLabel;
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
        var dayStatisticsService = GlobalContext.get(DayStatisticsService.class);

        var date = dayData.getDate();
        dateLabel.textProperty()
                .setValue(Utils.formatLocalDate(date));

        var expectedTotalInSecs = dayData.getExpectedTotalInSecsProperty().getValue();
        var duration = Utils.formatDuration(expectedTotalInSecs);
        expectedTotalField.textProperty().setValue(duration);

        var noteValue = dayData.getNote();
        commentField.textProperty().setValue(noteValue);

        saveButton.setOnMouseClicked(e -> {
            var newExpectedTotalInSecs = expectedTotalField.textProperty().get();
            var newExpectedLocalTime = LocalTime.parse(newExpectedTotalInSecs, LOCAL_TIME_FORMATTER);

            dayData.getExpectedTotalInSecsProperty().setValue(newExpectedLocalTime.toSecondOfDay());
            dayData.setNote(commentField.getText());

            dayStatisticsService.save();

            stage.close();
        });

        cancelButton.setOnMouseClicked(e -> stage.close());
    }
}
