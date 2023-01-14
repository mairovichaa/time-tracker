package time_tracker.component.stopwatch.record;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.config.GlobalContext;
import time_tracker.model.StopWatchAppState;
import time_tracker.service.StopwatchRecordService;

import static time_tracker.component.Utils.load;

@Log
public class CreateRecordVBox extends VBox {

    @FXML
    private MFXTextField recordDateTextField;
    @FXML
    private MFXTextField recordNameTextField;

    private final StopwatchRecordService stopwatchRecordService;

    public CreateRecordVBox() {
        load("/fxml/stopwatch/record/CreateRecordVBox.fxml", this);

        this.stopwatchRecordService = GlobalContext.get(StopwatchRecordService.class);

        var stopWatchAppState = GlobalContext.get(StopWatchAppState.class);
        recordDateTextField.textProperty()
                .bind(new StringBinding() {
                    {
                        bind(stopWatchAppState.getChosenDateProperty());
                    }

                    @Override
                    protected String computeValue() {
                        var chosenDate = stopWatchAppState.getChosenDateProperty().getValue();
                        return Utils.formatLocalDate(chosenDate);
                    }
                });
    }

    @FXML
    private void create() {
        log.fine(() -> "addStopwatchButton is clicked");
        var stopwatchName = recordNameTextField.getText();
        stopwatchRecordService.create(stopwatchName);
        stopwatchRecordService.store();
        recordNameTextField.clear();
    }
}
