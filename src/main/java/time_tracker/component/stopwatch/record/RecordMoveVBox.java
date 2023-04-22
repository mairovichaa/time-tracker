package time_tracker.component.stopwatch.record;

import io.github.palexdev.materialfx.controls.MFXDatePicker;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.annotation.NonNull;
import time_tracker.config.GlobalContext;
import time_tracker.model.StopwatchRecord;
import time_tracker.service.StopwatchRecordService;

import static time_tracker.component.Utils.load;

@Log
public class RecordMoveVBox extends VBox {

    @FXML
    private Label recordDateLabel;
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
        var currentDateFormatted = Utils.formatLocalDate(currentDate);
        recordDateLabel.setText(currentDateFormatted);
    }

    @FXML
    protected void save() {
        log.fine("'Save' button is clicked");
        var newDate = datePicker.getValue();
        var stopwatchRecordService = GlobalContext.get(StopwatchRecordService.class);
        stopwatchRecordService.moveToDate(record, newDate);

        stage.close();
    }

    @FXML
    protected void cancel() {
        log.fine("'Cancel' button is clicked");
        stage.close();
    }
}
