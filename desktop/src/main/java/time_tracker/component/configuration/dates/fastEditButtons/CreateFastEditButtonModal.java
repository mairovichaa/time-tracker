package time_tracker.component.configuration.dates.fastEditButtons;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.config.properties.StopwatchProperties.FastEditButtonProperties;
import time_tracker.controller.configuration.ConfigurationController;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;

@Log
public class CreateFastEditButtonModal extends VBox {

    @FXML
    private MFXTextField fastEditButtonTextField;
    @FXML
    private MFXTextField fastEditButtonHoursTextField;
    @FXML
    private MFXTextField fastEditButtonMinutesTextField;
    @FXML
    private MFXTextField fastEditButtonSecondsTextField;

    private final Stage stage;
    private final ConfigurationController configurationController;

    public CreateFastEditButtonModal(@NonNull Stage stage) {
        load("/fxml/configuration/dates/fastEditButtons/CreateFastEditButtonModal.fxml", this);
        this.stage = stage;
        configurationController = CONTEXT.get(ConfigurationController.class);
    }

    @FXML
    private void add() {
        log.fine(() -> "'addDefaultRecord' is clicked");

        var props = new FastEditButtonProperties();
        props.setName(fastEditButtonTextField.getText());
        String expected = String.format("%sH%sM%sS",
                fastEditButtonHoursTextField.getText(),
                fastEditButtonMinutesTextField.getText(),
                fastEditButtonSecondsTextField.getText());
        props.setExpected(expected);

        configurationController.addFastEditButton(props);
        stage.close();
    }

    @FXML
    private void cancel() {
        log.fine(() -> "cancelButton is clicked");
        stage.close();
    }
}
