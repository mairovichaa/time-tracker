package time_tracker.model;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.LongBinding;
import javafx.beans.binding.LongExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.annotation.Nullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Log
public class StopwatchRecord {

    @NonNull
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private LocalDate date;

    @NonNull
    private List<StopwatchRecordMeasurement> measurements = new ArrayList<>();

    @NonNull
    private ObservableList<StopwatchRecordMeasurement> measurementsProperty = FXCollections.observableArrayList(measurements);

    private StopwatchRecordMeasurement measurementInProgress;

    private ObjectProperty<StopwatchRecordMeasurement> measurementInProgressProperty = new SimpleObjectProperty<>();

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

    private BooleanBinding hasMeasurementInProgress = new BooleanBinding() {
        {
            bind(measurementInProgressProperty);
        }

        @Override
        protected boolean computeValue() {
            return measurementInProgressProperty.getValue() != null;
        }
    };

    public void setMeasurementInProgress(@Nullable final StopwatchRecordMeasurement measurementInProgress) {
        this.measurementInProgress = measurementInProgress;
        measurementInProgressProperty.setValue(measurementInProgress);
    }

}
