package time_tracker.component.configuration.defaultRecordNames;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.controller.configuration.ConfigurationController;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;

@Log
public class CreateDefaultRecordModal extends VBox {

    @FXML
    private MFXTextField recordNameTextField;
    private final Stage stage;
    private final ConfigurationController configurationController;


    public CreateDefaultRecordModal(@NonNull Stage stage) {
        load("/fxml/configuration/defaultRecordNames/CreateDefaultRecordModal.fxml", this);
        this.stage = stage;
        configurationController = CONTEXT.get(ConfigurationController.class);
    }

    @FXML
    private void add() {
        log.fine(() -> "'addDefaultRecord' is clicked");
        var recordName = recordNameTextField.getText();
        configurationController.addDefaultRecord(recordName);
        recordNameTextField.clear();
        stage.close();
    }

    @FXML
    private void cancel() {
        log.fine(() -> "cancelButton is clicked");
        stage.close();
    }
}
