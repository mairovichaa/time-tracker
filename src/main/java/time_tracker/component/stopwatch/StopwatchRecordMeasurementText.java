package time_tracker.component.stopwatch;

import javafx.scene.text.Text;
import lombok.NonNull;
import time_tracker.model.StopwatchRecordMeasurement;

public class StopwatchRecordMeasurementText extends Text {
    public StopwatchRecordMeasurementText(@NonNull final StopwatchRecordMeasurement measurement) {
        this.textProperty()
                .bind(measurement.getStopwatchStringProperty());
    }

}
