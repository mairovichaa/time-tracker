package time_tracker.model.mapper;

import time_tracker.domain.Measurement;
import time_tracker.model.StopwatchRecordMeasurement;

public class MeasurementToStopwatchRecordMeasurementConverter implements Converter<Measurement, StopwatchRecordMeasurement>{

    @Override
    public StopwatchRecordMeasurement convert(Measurement src) {
        var dst = new StopwatchRecordMeasurement();

        dst.setId(src.getId());
        dst.setStartedAt(src.getStartedAt());
        dst.setStoppedAt(src.getStoppedAt());
        dst.getNoteProperty().set(src.getNote());

        return dst;
    }
}
