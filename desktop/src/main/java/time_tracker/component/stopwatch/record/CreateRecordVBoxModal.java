package time_tracker.component.stopwatch.record;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.service.StopwatchRecordService;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;

@Log
public class CreateRecordVBoxModal extends VBox {

    @FXML
    private MFXTextField recordNameTextField;

    private final StopwatchRecordService stopwatchRecordService;
    private final Stage stage;

    public CreateRecordVBoxModal(@NonNull Stage stage) {
        load("/fxml/stopwatch/record/CreateRecordVBoxModal.fxml", this);
        this.stopwatchRecordService = CONTEXT.get(StopwatchRecordService.class);
        this.stage = stage;
    }

    @FXML
    private void create() {
        log.fine(() -> "addStopwatchButton is clicked");
        var stopwatchName = recordNameTextField.getText();
        // TODO pass date here
        stopwatchRecordService.create(stopwatchName);
        stopwatchRecordService.store();
        recordNameTextField.clear();
        stage.close();
    }

    @FXML
    private void cancel() {
        log.fine(() -> "cancelButton is clicked");
        stage.close();
    }
}
