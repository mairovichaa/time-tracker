package time_tracker.service.report.groupedByRecord;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import time_tracker.Utils;
import time_tracker.model.StopwatchRecord;
import time_tracker.service.report.common.StopwatchMeasurementExportDto;
import time_tracker.service.report.common.StopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ExportDtoMappers {

    private final StopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper stopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper;

    @NonNull
    public Map<String, List<StopwatchRecordExportDto>> map(@NonNull final Map<String, List<StopwatchRecord>> src) {
        Map<String, List<StopwatchRecordExportDto>> dst = new LinkedHashMap<>();
        src.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(nameToRecords -> {
                    String name = nameToRecords.getKey();
                    List<StopwatchRecordExportDto> stopwatchRecordExportDtos = nameToRecords.getValue()
                            .stream()
                            .map(this::map)
                            .collect(Collectors.toList());
                    dst.put(name, stopwatchRecordExportDtos);
                });
        return dst;
    }

    @NonNull
    private StopwatchRecordExportDto map(@NonNull final StopwatchRecord record) {
        var stopwatchRecordExportDto = new StopwatchRecordExportDto();
        stopwatchRecordExportDto.setDate(Utils.formatLocalDate(record.getDate()));
        List<StopwatchMeasurementExportDto> stopwatchMeasurementExportDtos = stopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper.map(record.getMeasurementsProperty());
        stopwatchRecordExportDto.setMeasurements(stopwatchMeasurementExportDtos);
        return stopwatchRecordExportDto;
    }
}
