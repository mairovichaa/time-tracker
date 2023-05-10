package time_tracker.component.stopwatch.record;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.common.GlobalContext;
import time_tracker.model.StopwatchRecord;
import time_tracker.service.StopwatchRecordService;

import static time_tracker.component.Utils.load;

@Log
public class RecordRenameVBox extends VBox {

    @FXML
    private MFXTextField nameTextField;
    private final Stage stage;
    private final StopwatchRecord record;

    public RecordRenameVBox(
            @NonNull final StopwatchRecord record,
            @NonNull final Stage stage
    ) {
        load("/fxml/stopwatch/record/RecordRenameVBox.fxml", this);
        this.stage = stage;
        this.record = record;
    }

    @FXML
    protected void save() {
        log.fine("'Save' button is clicked");

        var newName = nameTextField.getText();
        record.setName(newName);

        var stopwatchRecordService = GlobalContext.get(StopwatchRecordService.class);
        stopwatchRecordService.store(record);

        stage.close();
    }

    @FXML
    protected void cancel() {
        log.fine("'Cancel' button is clicked");
        stage.close();
    }
}
