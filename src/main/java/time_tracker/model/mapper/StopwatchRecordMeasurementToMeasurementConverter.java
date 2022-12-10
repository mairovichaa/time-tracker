package time_tracker.model.mapper;

import time_tracker.annotation.NonNull;
import time_tracker.domain.Measurement;
import time_tracker.model.StopwatchRecordMeasurement;

public class StopwatchRecordMeasurementToMeasurementConverter implements Converter<StopwatchRecordMeasurement, Measurement> {

    @NonNull
    public Measurement convert(@NonNull final StopwatchRecordMeasurement src) {
        var measurement = new Measurement();
        measurement.setId(src.getId());
        measurement.setNote(src.getNote());
        measurement.setStartedAt(src.getStartedAt());
        measurement.setStoppedAt(src.getStoppedAt());
        return measurement;
    }

}
