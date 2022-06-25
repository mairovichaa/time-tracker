package time_tracker.component.stopwatch;

import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.NonNull;
import time_tracker.model.StopwatchRecordMeasurement;

public class StopwatchRecordMeasurementInProgressVBox extends VBox {

    public StopwatchRecordMeasurementInProgressVBox(@NonNull final StopwatchRecordMeasurement measurement) {
        var measurementText = new StopwatchRecordMeasurementText(measurement);

        var noteHBox = new HBox();
        var noteText = new Text("Note:");
        var measurementNoteInput = new TextField();
        measurementNoteInput.textProperty()
                .bindBidirectional(measurement.getNoteProperty());

        noteHBox.getChildren().addAll(noteText, measurementNoteInput);
        this.getChildren().addAll(measurementText, noteHBox);
    }

}
