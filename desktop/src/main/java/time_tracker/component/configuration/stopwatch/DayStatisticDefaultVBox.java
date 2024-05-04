package time_tracker.component.configuration.stopwatch;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.common.annotation.Nullable;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.service.ConfigurationService;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;

@Log
public class DayStatisticDefaultVBox extends VBox {

    @FXML
    protected MFXTextField expectedWorkTimeTextField;
    @FXML
    protected MFXTextField commentTextField;
    @FXML
    protected Label expectedWorkTimeErrorLabel;
    @FXML
    protected MFXButton saveButton;
    @FXML
    protected MFXButton cancelButton;

    private final StopwatchProperties.DefaultDayStatisticProperties defaultData;

    public DayStatisticDefaultVBox() {
        load("/fxml/configuration/stopwatch/DayStatisticDefaultVBox.fxml", this);

        StopwatchProperties stopwatchProperties = CONTEXT.get(StopwatchProperties.class);
        defaultData = stopwatchProperties.getStopwatch().getDayStatistic().getDefaultData();

        setCurrentValuesOfPropertiesToInputs();

        expectedWorkTimeTextField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    String comment = commentTextField.getText();
                    disableButtonsIfValuesAreNotChangedOrInvalid(comment, newValue);
                });

        commentTextField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    String expectedWorkTimeTextFieldText = expectedWorkTimeTextField.getText();
                    disableButtonsIfValuesAreNotChangedOrInvalid(newValue, expectedWorkTimeTextFieldText);
                });
    }

    private void disableButtonsIfValuesAreNotChangedOrInvalid(
            @NonNull final String comment,
            @NonNull final String duration) {
        String durationInConfigsFormat = getDurationInPropertiesFormatOrNull(duration);
        if (durationInConfigsFormat == null) {
            expectedWorkTimeErrorLabel.setText("Wrong format: H:MM or HH:MM is expected. H - hours, M - minutes.");
            expectedWorkTimeErrorLabel.setManaged(true);
            expectedWorkTimeErrorLabel.setVisible(true);
        } else {
            expectedWorkTimeErrorLabel.setManaged(false);
            expectedWorkTimeErrorLabel.setVisible(false);
        }

        boolean areValuesSame = defaultData.getComment().equals(comment) && defaultData.getExpectedWorkTime().equals(durationInConfigsFormat);
        if (areValuesSame) {
            saveButton.setDisable(true);
            cancelButton.setDisable(true);
        } else {
            if (durationInConfigsFormat == null) {
                saveButton.setDisable(true);
            } else {
                saveButton.setDisable(false);
            }
            cancelButton.setDisable(false);
        }
    }

    @Nullable
    private String getDurationInPropertiesFormatOrNull(@NonNull final String duration) {
        try {
            String[] split = duration.split(":");
            String hours = split[0];
            if (0 > Integer.parseInt(hours) || Integer.parseInt(hours) > 23) {
                log.severe(() -> "hours '" + hours + "' is invalid value. Expected value in range between 0 and 23 inclusively");
                return null;
            }
            String minutes = split[1];
            if (0 > Integer.parseInt(minutes) || Integer.parseInt(minutes) > 59) {
                log.severe(() -> "minutes '" + hours + "' is invalid value. Expected value in range between 0 and 59 inclusively");
                return null;
            }
            return hours + "H" + minutes + "M";
        } catch (Exception exception) {
            log.fine(() -> "Can't parse expected: " + exception.getMessage());
            return null;
        }
    }

    @FXML
    protected void save() {
        log.fine("'Save' button is clicked");
        ConfigurationService configurationService = CONTEXT.get(ConfigurationService.class);

        String duration = expectedWorkTimeTextField.getText();
        String durationInConfigsFormat = getDurationInPropertiesFormatOrNull(duration);
        String comment = commentTextField.getText();

        configurationService.updateStopwatchDayStatisticDefaultProperties(durationInConfigsFormat, comment);
        disableButtonsIfValuesAreNotChangedOrInvalid(comment, duration);
    }

    @FXML
    protected void cancel() {
        log.fine("'Cancel' button is clicked");
        setCurrentValuesOfPropertiesToInputs();
    }

    private void setCurrentValuesOfPropertiesToInputs() {
        String expectedWorkTime = defaultData.getExpectedWorkTime()
                .replace("H", ":")
                .replace("M", "");
        expectedWorkTimeTextField.setText(expectedWorkTime);
        commentTextField.setText(defaultData.getComment());
    }
}
