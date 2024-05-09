package time_tracker.service.converter;

import lombok.RequiredArgsConstructor;
import time_tracker.common.annotation.NonNull;
import time_tracker.domain.Record;
import time_tracker.model.StopwatchRecord;

@RequiredArgsConstructor
public class RecordToStopwatchRecordConverter implements Converter<Record, StopwatchRecord> {

    private final MeasurementToStopwatchRecordMeasurementConverter measurementToStopwatchRecordMeasurementConverter;

    @Override
    @NonNull
    public StopwatchRecord convert(@NonNull Record src) {
        var dst = new StopwatchRecord();
        dst.setId(src.getId());
        dst.setName(src.getName());
        dst.setDate(src.getDate());
        dst.getTrackedProperty().setValue(src.isTracked());

        var measurements = measurementToStopwatchRecordMeasurementConverter.convert(src.getMeasurements());
        dst.getMeasurementsProperty().addAll(measurements);
        return dst;
    }
}
