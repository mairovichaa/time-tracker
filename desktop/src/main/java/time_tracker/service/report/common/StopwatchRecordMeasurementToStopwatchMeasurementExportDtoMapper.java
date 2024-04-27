package time_tracker.service.report.common;

import lombok.NonNull;
import time_tracker.Utils;
import time_tracker.model.StopwatchRecordMeasurement;

import java.util.List;
import java.util.stream.Collectors;

import static time_tracker.Constants.DATA_TIME_FORMATTER;

public class StopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper {

    @NonNull
    public List<StopwatchMeasurementExportDto> map(@NonNull final List<StopwatchRecordMeasurement> measurements) {
        return measurements.stream().map(this::map).collect(Collectors.toList());
    }

    @NonNull
    public StopwatchMeasurementExportDto map(@NonNull final StopwatchRecordMeasurement src) {
        var dst = new StopwatchMeasurementExportDto();
        dst.setComment(src.getNote());
        dst.setStartedAt(DATA_TIME_FORMATTER.format(src.getStartedAt()));
        dst.setStoppedAt(DATA_TIME_FORMATTER.format(src.getStoppedAt()));
        dst.setDuration(Utils.formatDuration(src.getDuration()));
        return dst;
    }
}
