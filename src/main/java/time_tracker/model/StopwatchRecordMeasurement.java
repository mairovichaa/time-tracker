package time_tracker.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import lombok.Data;
import time_tracker.annotation.NonNull;
import time_tracker.annotation.Nullable;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicBoolean;

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

    @NonNull
    private SimpleLongProperty durationProperty = new SimpleLongProperty(0);

    private BooleanProperty internalChanged = new SimpleBooleanProperty(false);
    private AtomicBoolean change = new AtomicBoolean(false);


    {
        this.internalChanged.bind(
                Bindings.createBooleanBinding(
                        // make it the opposite to be fire change event for sure
                        () -> {
                            var val = change.get();
                            change.set(!val);
                            return !val;
                        },
                        this.startedAtProperty, this.stoppedAtProperty, this.noteProperty, this.durationProperty
                )
        );
    }


    public void setStartedAt(@NonNull final LocalTime startedAt) {
        this.startedAt = startedAt;
        this.startedAtProperty.set(startedAt);

        if (stoppedAt != null) {
            var duration = getDuration();
            getDurationProperty().set(duration.getSeconds());
        }
    }

    public void setStoppedAt(@NonNull final LocalTime stoppedAt) {
        this.stoppedAt = stoppedAt;
        this.stoppedAtProperty.set(stoppedAt);

        if (startedAt != null) {
            var duration = getDuration();
            getDurationProperty().set(duration.getSeconds());
        }
    }

    public String getNote() {
        return noteProperty.getValue();
    }

    public Duration getDuration() {
        return Duration.between(startedAt, stoppedAt);
    }


}
