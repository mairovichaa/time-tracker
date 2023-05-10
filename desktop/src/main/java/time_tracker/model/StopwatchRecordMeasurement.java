package time_tracker.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import lombok.Getter;
import time_tracker.common.annotation.NonNull;
import time_tracker.common.annotation.Nullable;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicBoolean;

public class StopwatchRecordMeasurement {

    private final SimpleLongProperty idProperty = new SimpleLongProperty(-1);

    public Long getId() {
        return idProperty.getValue();
    }

    public void setId(@NonNull Long id) {
        idProperty.setValue(id);
    }

    @NonNull
    @Getter
    private final ObjectProperty<LocalTime> startedAtProperty = new SimpleObjectProperty<>();

    @Nullable
    public LocalTime getStartedAt() {
        return startedAtProperty.get();
    }

    public void setStartedAt(@NonNull final LocalTime startedAt) {
        this.startedAtProperty.set(startedAt);

        if (getStoppedAt() != null) {
            var duration = getDuration();
            durationProperty.set(duration.getSeconds());
        }
    }

    @NonNull
    @Getter
    private final ObjectProperty<LocalTime> stoppedAtProperty = new SimpleObjectProperty<>();

    @Nullable
    public LocalTime getStoppedAt() {
        return stoppedAtProperty.get();
    }

    public void setStoppedAt(@NonNull final LocalTime stoppedAt) {
        this.stoppedAtProperty.set(stoppedAt);

        if (getStartedAt() != null) {
            var duration = getDuration();
            durationProperty.set(duration.getSeconds());
        }
    }

    @NonNull
    @Getter
    private final StringProperty noteProperty = new SimpleStringProperty("");

    public void setNote(@Nullable String note) {
        noteProperty.set(note);
    }

    @Nullable
    public String getNote() {
        return noteProperty.getValue();
    }

    @NonNull
    @Getter
    private final SimpleLongProperty durationProperty = new SimpleLongProperty(0);

    public Duration getDuration() {
        return Duration.between(getStartedAt(), getStoppedAt());
    }

    @Getter
    private final BooleanProperty isChanged = new SimpleBooleanProperty(false);

    private final AtomicBoolean change = new AtomicBoolean(false);

    {
        this.isChanged.bind(
                Bindings.createBooleanBinding(
                        // make it the opposite to be fire change event for sure
                        () -> {
                            var val = change.get();
                            change.set(!val);
                            return !val;
                        },
                        idProperty, startedAtProperty, stoppedAtProperty, noteProperty, durationProperty
                )
        );
    }

}
