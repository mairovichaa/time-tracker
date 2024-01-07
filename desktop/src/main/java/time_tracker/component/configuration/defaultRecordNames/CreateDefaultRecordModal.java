package time_tracker.component.configuration.defaultRecordNames;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.service.ConfigurationService;

import static time_tracker.component.Utils.load;

@Log
public class CreateDefaultRecordModal extends VBox {

    @FXML
    private MFXTextField recordNameTextField;
    private final Stage stage;
    private final ConfigurationService configurationService;

    public CreateDefaultRecordModal(
            @NonNull Stage stage,
            @NonNull ConfigurationService configurationService) {
        load("/fxml/configuration/defaultRecordNames/CreateDefaultRecordModal.fxml", this);
        this.stage = stage;
        this.configurationService = configurationService;
    }

    @FXML
    private void add() {
        log.fine(() -> "'addDefaultRecord' is clicked");
        var recordName = recordNameTextField.getText();
        configurationService.addDefaultRecord(recordName);
        recordNameTextField.clear();
        stage.close();
    }

    @FXML
    private void cancel() {
        log.fine(() -> "cancelButton is clicked");
        stage.close();
    }
}
