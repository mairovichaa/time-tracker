package time_tracker.model.mapper;

import lombok.RequiredArgsConstructor;
import time_tracker.annotation.NonNull;
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

        var measurements = measurementToStopwatchRecordMeasurementConverter.convert(src.getMeasurements());
        dst.getMeasurementsProperty().addAll(measurements);
        return dst;
    }
}
