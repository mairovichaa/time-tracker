package time_tracker.model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.LongBinding;
import javafx.beans.binding.LongExpression;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.common.annotation.Nullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Data
@Log
public class StopwatchRecord {

    @NonNull
    private Long id;

    @NonNull
    private StringProperty nameProperty = new SimpleStringProperty();

    // TODO make it a property
    @NonNull
    private LocalDate date;

    private ObjectProperty<LocalDate> dateProperty = new SimpleObjectProperty<>();

    @NonNull
    public ObjectProperty<LocalDate> getDateProperty() {
        return dateProperty;
    }

    public void setDate(@NonNull final LocalDate date) {
        getDateProperty().setValue(date);
        this.date = date;
    }

    private BooleanProperty trackedProperty = new SimpleBooleanProperty(false);

    public String getName() {
        return nameProperty.getValue();
    }

    public void setName(@NonNull final String name) {
        nameProperty.setValue(name);
    }

    public void setTracked(boolean isTracked) {
        trackedProperty.set(isTracked);
    }

    public boolean isTracked() {
        return trackedProperty.get();
    }

    @NonNull
    private ObservableList<StopwatchRecordMeasurement> measurementsProperty = FXCollections.observableArrayList(new ArrayList<>());

    private ObjectProperty<StopwatchRecordMeasurement> measurementInProgressProperty = new SimpleObjectProperty<>();

    @Nullable
    public StopwatchRecordMeasurement getMeasurementInProgress() {
        return measurementInProgressProperty.get();
    }

    public void setMeasurementInProgress(@Nullable final StopwatchRecordMeasurement measurementInProgress) {
        measurementInProgressProperty.setValue(measurementInProgress);
    }

    private BooleanBinding hasMeasurementInProgressProperty = new BooleanBinding() {
        {
            bind(measurementInProgressProperty);
        }

        @Override
        protected boolean computeValue() {
            return measurementInProgressProperty.getValue() != null;
        }
    };

    private LongBinding measurementsTotalInSecsLongBinding = new LongBinding() {
        @Nullable
        private SimpleLongProperty measurementInProgressDurationProperty;

        @NonNull
        private List<BooleanProperty> boundInternalChanged = Collections.emptyList();

        {
            this.bind(getMeasurementsProperty(), getMeasurementInProgressProperty());
        }

        @Override
        protected long computeValue() {
            // TODO it shouldn't be done each time - find a way to optimize it
            if (!boundInternalChanged.isEmpty()) {
                boundInternalChanged.forEach(this::unbind);
            }

            boundInternalChanged = getMeasurementsProperty().stream()
                    .map(StopwatchRecordMeasurement::getIsChanged)
                    .collect(Collectors.toList());
            boundInternalChanged.forEach(this::bind);

            var stopwatchRecordMeasurement = getMeasurementInProgressProperty().get();
            if (stopwatchRecordMeasurement == null && measurementInProgressDurationProperty != null) {
                log.fine("Unbind in progress duration property");
                this.unbind(measurementInProgressDurationProperty);
                measurementInProgressDurationProperty = null;
            }
            if (stopwatchRecordMeasurement != null && measurementInProgressDurationProperty == null) {
                log.fine("Bind in progress duration property");
                measurementInProgressDurationProperty = stopwatchRecordMeasurement.getDurationProperty();
                this.bind(measurementInProgressDurationProperty);
            }

            var totalSeconds = getMeasurementsProperty().stream()
                    .map(StopwatchRecordMeasurement::getDurationProperty)
                    .mapToLong(LongExpression::get)
                    .sum();

            if (measurementInProgressDurationProperty != null) {
                totalSeconds += measurementInProgressDurationProperty.getValue();
            }
            return totalSeconds;
        }
    };

    public long getMeasurementsTotalInSecs() {
        return getMeasurementsTotalInSecsLongBinding().get();
    }

    private final LongProperty isChangedProperty = new SimpleLongProperty(0);
    private final AtomicLong change = new AtomicLong(0);

    {
        this.isChangedProperty.bind(
                Bindings.createLongBinding(
                        () -> {
                            var changeNumber = change.getAndIncrement();
                            log.fine(() -> "record with id = " + getId() + " has been changed. Change number = " + changeNumber);
                            return changeNumber;
                        },
                        this.trackedProperty, this.measurementsProperty, this.measurementInProgressProperty, this.measurementsTotalInSecsLongBinding, this.dateProperty
                )
        );
        this.isChangedProperty.addListener((observable, oldValue, newValue) -> log.fine("Listener to turn off lazy calculation"));
        this.trackedProperty.addListener((observable, oldValue, newValue) -> log.fine("Listener to turn off lazy calculation"));

    }
}
