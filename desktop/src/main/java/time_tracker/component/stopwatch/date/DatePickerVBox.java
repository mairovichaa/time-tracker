package time_tracker.component.stopwatch.date;

import io.github.palexdev.materialfx.beans.Alignment;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import time_tracker.TimeTrackerApp;
import time_tracker.common.GlobalContext;
import time_tracker.component.stopwatch.record.CreateRecordVBoxModal;
import time_tracker.model.StopWatchAppState;

import java.time.LocalDate;

import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static time_tracker.component.Utils.load;

@Log
public class DatePickerVBox extends VBox {

    @FXML
    private MFXDatePicker datePicker;

    private final StopWatchAppState stopWatchAppState;

    public DatePickerVBox() {
        load("/fxml/stopwatch/date/DatePickerVBox.fxml", this);

        stopWatchAppState = GlobalContext.get(StopWatchAppState.class);

        var date = getInitialDate();
        stopWatchAppState.setChosenDate(date);
        datePicker.setValue(date);

        stopWatchAppState.getChosenDateProperty().addListener(
                (observable, oldValue, newValue) -> datePicker.setValue(newValue));

        datePicker.popupAlignmentProperty()
                .set(Alignment.of(HPos.RIGHT, VPos.BOTTOM));
    }

    private LocalDate getInitialDate() {
        return stopWatchAppState.getDateToDayData()
                .keySet()
                .stream()
                .max(comparing(identity()))
                .orElse(LocalDate.now());
    }

    @FXML
    private void pickNewDate(ActionEvent event) {
        event.consume();

        LocalDate pickedValue = datePicker.getValue();
        log.fine(() -> "Choose new date - " + pickedValue);

        stopWatchAppState.setChosenDate(pickedValue);
    }

    @FXML
    private void addRecord() {
        log.fine("'Add record' button is clicked");

        var dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(TimeTrackerApp.primaryStage);
        var dialogVbox = new CreateRecordVBoxModal(dialog);
        var dialogScene = new Scene(dialogVbox);
        dialog.setScene(dialogScene);
        dialog.show();
    }
}
