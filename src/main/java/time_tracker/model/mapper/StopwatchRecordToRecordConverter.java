package time_tracker.model.mapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.domain.Record;
import time_tracker.model.StopwatchRecord;

import java.util.ArrayList;

@Data
@Log
@RequiredArgsConstructor
public class StopwatchRecordToRecordConverter implements Converter<StopwatchRecord, Record> {

    private final StopwatchRecordMeasurementToMeasurementConverter stopwatchRecordMeasurementToMeasurementConverter;

    @NonNull
    public Record convert(@NonNull final StopwatchRecord src) {
        var dst = new Record();
        dst.setId(src.getId());
        dst.setName(src.getName());
        dst.setDate(src.getDate());

        var converted = stopwatchRecordMeasurementToMeasurementConverter.convert(src.getMeasurementsProperty());
        var newMeasurements = new ArrayList<>(converted);
        dst.setMeasurements(newMeasurements);

        return dst;
    }

}
