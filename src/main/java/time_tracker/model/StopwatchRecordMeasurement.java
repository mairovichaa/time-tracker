package time_tracker.model;

import javafx.beans.property.*;
import lombok.Data;
import time_tracker.annotation.NonNull;
import time_tracker.annotation.Nullable;

import java.time.Duration;
import java.time.LocalTime;

@Data
public class StopwatchRecordMeasurement {
    @NonNull
    private LocalTime startedAt;
    @NonNull
    private ObjectProperty<LocalTime> startedAtProperty = new SimpleObjectProperty<>();
    @Nullable
    private LocalTime stoppedAt;
    @NonNull
    private ObjectProperty<LocalTime> stoppedAtProperty = new SimpleObjectProperty<>();

    @NonNull
    private StringProperty noteProperty = new SimpleStringProperty("");


    public void setStartedAt(@NonNull final LocalTime startedAt) {
        this.startedAt = startedAt;
        this.startedAtProperty.set(startedAt);
    }

    public void setStoppedAt(@NonNull final LocalTime stoppedAt) {
        this.stoppedAt = stoppedAt;
        this.stoppedAtProperty.set(stoppedAt);
    }

    public String getNote() {
        return noteProperty.getValue();
    }

    public Duration getDuration() {
        return Duration.between(startedAt, stoppedAt);
    }

    private SimpleLongProperty durationProperty = new SimpleLongProperty(0);

}
