package time_tracker.component.stopwatch;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.ButtonType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.common.GlobalContext;
import time_tracker.common.annotation.NonNull;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.config.properties.StopwatchProperties.FastEditButtonProperties;
import time_tracker.model.DayData;
import time_tracker.service.DayDataService;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.util.Comparator.comparing;
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
    private FlowPane fastEditButtons;

    private final DayData dayData;
    private final Stage stage;
    private final DayDataService dayDataService;

    public DayDataEditVBox(
            @NonNull final DayData dayData,
            @NonNull final Stage stage
    ) {
        load("/fxml/stopwatch/DayDataEditVBox.fxml", this);
        this.dayData = dayData;
        this.stage = stage;
        this.dayDataService = GlobalContext.get(DayDataService.class);

        var date = dayData.getDate();
        dateLabel.textProperty().setValue(Utils.formatLocalDate(date));

        var expectedTotalInSecs = dayData.getExpectedTotalInSecsProperty().getValue();
        var duration = Utils.formatDuration(expectedTotalInSecs);
        expectedTotalField.textProperty().setValue(duration);

        var noteValue = dayData.getNote();
        commentField.textProperty().setValue(noteValue);

        createFastEditFlowPane();
    }

    private void createFastEditFlowPane() {
        var stopwatchProperties = GlobalContext.get(StopwatchProperties.class);
        stopwatchProperties.getDates()
                .getFastEditButtons()
                .stream()
                .sorted(comparing(FastEditButtonProperties::getName))
                .map(this::createFastEditButton)
                .forEach(it -> fastEditButtons.getChildren().add(it));
    }

    @NonNull
    private MFXButton createFastEditButton(@NonNull final FastEditButtonProperties properties) {
        var buttonName = properties.getName();
        var button = new MFXButton(buttonName);
        button.setButtonType(ButtonType.RAISED);
        button.setOnMouseClicked(e -> {
            log.fine(() -> "'Holiday' button is clicked for dayData = " + dayData.getId());

            var expectedDuration = Duration.parse("PT" + properties.getExpected());
            dayData.setExpected(expectedDuration);
            dayData.setNote(buttonName);

            saveDayData();
        });
        return button;
    }

    @FXML
    public void save() {
        var newExpectedTotalInSecs = expectedTotalField.textProperty().get();
        var newExpectedLocalTime = LocalTime.parse(newExpectedTotalInSecs, LOCAL_TIME_FORMATTER);

        dayData.getExpectedTotalInSecsProperty().setValue(newExpectedLocalTime.toSecondOfDay());
        dayData.setNote(commentField.getText());

        saveDayData();
    }

    private void saveDayData() {
        dayDataService.save(dayData);
        stage.close();
    }

    @FXML
    public void cancel() {
        stage.close();
    }
}
