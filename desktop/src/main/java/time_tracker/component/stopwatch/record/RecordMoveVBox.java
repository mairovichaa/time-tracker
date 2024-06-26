package time_tracker.component.stopwatch.record;

import io.github.palexdev.materialfx.controls.MFXDatePicker;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.model.StopwatchRecord;
import time_tracker.service.StopwatchRecordService;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;

@Log
public class RecordMoveVBox extends VBox {

    @FXML
    private MFXDatePicker datePicker;

    private final Stage stage;
    private final StopwatchRecord record;

    public RecordMoveVBox(
            @NonNull final StopwatchRecord record,
            @NonNull final Stage stage
    ) {
        load("/fxml/stopwatch/record/RecordMoveVBox.fxml", this);
        this.stage = stage;
        this.record = record;
        var currentDate = record.getDate();
        datePicker.setValue(currentDate);
    }

    @FXML
    protected void save() {
        log.fine("'Save' button is clicked");
        var newDate = datePicker.getValue();
        var stopwatchRecordService = CONTEXT.get(StopwatchRecordService.class);
        stopwatchRecordService.moveToDate(record, newDate);

        stage.close();
    }

    @FXML
    protected void cancel() {
        log.fine("'Cancel' button is clicked");
        stage.close();
    }
}
