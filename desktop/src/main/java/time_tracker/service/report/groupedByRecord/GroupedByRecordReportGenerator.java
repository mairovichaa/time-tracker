package time_tracker.service.report.groupedByRecord;

import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.exception.report.ReportGenerationException;
import time_tracker.model.ReportState;
import time_tracker.model.StopwatchRecord;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static time_tracker.config.properties.StopwatchProperties.ReportProperties.ExportFormat.JSON;

@Log
@RequiredArgsConstructor
public class GroupedByRecordReportGenerator {

    private final CustomFormatWriter customFormatWriter;
    private final ObjectWriter prettyJsonWriter;
    private final ExportDtoMappers mapper;

    @NonNull
    public String generate(@NonNull final ReportState reportState) {
        Map<String, List<StopwatchRecord>> recordNameToRecords = reportState.getRecordNameToRecords();
        Map<String, List<StopwatchRecordExportDto>> mappedChosenRecords = mapper.map(recordNameToRecords);
        if (reportState.getExportFormat().get() == JSON) {
            try {
                return prettyJsonWriter.writeValueAsString(mappedChosenRecords);
            } catch (IOException e) {
                log.severe(e::getMessage);
                throw new ReportGenerationException(e);
            }
        } else {
            return customFormatWriter.writeValueAsString(mappedChosenRecords);
        }
    }
}
