package time_tracker.service.report.groupedByRecord;

import lombok.RequiredArgsConstructor;
import time_tracker.common.annotation.NonNull;
import time_tracker.service.report.common.StopwatchMeasurementExportDtoCustomFormatWriter;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CustomFormatWriter {

    private final StopwatchMeasurementExportDtoCustomFormatWriter stopwatchMeasurementExportDtoCustomFormatWriter;

    @NonNull
    public String writeValueAsString(@NonNull Map<String, List<StopwatchRecordExportDto>> nameToRecords) {
        if (nameToRecords.isEmpty()) {
            return "No records";
        }
        var result = new StringBuilder();
        nameToRecords.forEach((name, records) -> {
            result.append(name)
                    .append(System.lineSeparator());
            records.forEach(record -> appendRecord(record, result));
            result.append(System.lineSeparator());
        });
        return result.toString();
    }

    private void appendRecord(
            @NonNull final StopwatchRecordExportDto record,
            @NonNull final StringBuilder result) {
        result.append("\t")
                .append(record.getDate())
                .append(System.lineSeparator());
        stopwatchMeasurementExportDtoCustomFormatWriter.append(record.getMeasurements(), result);

    }
}
