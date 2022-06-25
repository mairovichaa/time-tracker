package time_tracker.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;
import lombok.NonNull;

import java.time.Duration;
import java.time.LocalTime;

@Data
public class StopwatchRecordMeasurement {
    private LocalTime startedAt;
    private LocalTime stoppedAt;

    // TODO should be done in another way - there should be object binding and its conversion to string
    private StringProperty stopwatchStringProperty = new SimpleStringProperty();

    public void setMeasurementString(@NonNull String measurementString) {
        stopwatchStringProperty.set(measurementString);
    }

    public Duration getDuration() {
        return Duration.between(startedAt, stoppedAt);
    }

    private SimpleLongProperty durationProperty = new SimpleLongProperty(0);

}
