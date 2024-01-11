package time_tracker.component.configuration.dates.fastEditButtons;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.config.properties.StopwatchProperties.FastEditButtonProperties;
import time_tracker.service.ConfigurationService;

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
    private final ConfigurationService configurationService;

    public CreateFastEditButtonModal(
            @NonNull Stage stage,
            @NonNull ConfigurationService configurationService) {
        load("/fxml/configuration/dates/fastEditButtons/CreateFastEditButtonModal.fxml", this);
        this.stage = stage;
        this.configurationService = configurationService;
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

        configurationService.addFastEditButton(props);
        stage.close();
    }

    @FXML
    private void cancel() {
        log.fine(() -> "cancelButton is clicked");
        stage.close();
    }
}
