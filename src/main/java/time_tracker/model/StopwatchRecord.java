package time_tracker.model;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.LongBinding;
import javafx.beans.binding.LongExpression;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.java.Log;
import time_tracker.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

@Data
@Log
public class StopwatchRecord {

    @NonNull
    private String name;

    @NonNull
    private List<StopwatchRecordMeasurement> measurements = new ArrayList<>();

    @NonNull
    private ObservableList<StopwatchRecordMeasurement> measurementsProperty = FXCollections.observableArrayList(measurements);

    private StopwatchRecordMeasurement measurementInProgress;

    private ObjectProperty<StopwatchRecordMeasurement> measurementInProgressProperty = new SimpleObjectProperty<>();

    private LongBinding measurementsTotalInSecsLongBinding = new LongBinding() {
        @Nullable
        private SimpleLongProperty measurementInProgressDurationProperty;

        {
            this.bind(getMeasurementsProperty(), getMeasurementInProgressProperty());
        }

        @Override
        protected long computeValue() {
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
