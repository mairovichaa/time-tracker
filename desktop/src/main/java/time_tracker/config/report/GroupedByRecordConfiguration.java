package time_tracker.config.report;

import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.common.di.Bean;
import time_tracker.service.report.common.StopwatchMeasurementExportDtoCustomFormatWriter;
import time_tracker.service.report.common.StopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper;
import time_tracker.service.report.groupedByRecord.CustomFormatWriter;
import time_tracker.service.report.groupedByRecord.ExportDtoMappers;
import time_tracker.service.report.groupedByRecord.GroupedByRecordReportGenerator;

import java.util.logging.Level;

@Log
public class GroupedByRecordConfiguration {

    @NonNull
    @Bean
    public GroupedByRecordReportGenerator groupedByRecordReportGenerator(
            @NonNull final CustomFormatWriter customFormatWriter,
            @NonNull final ObjectWriter prettyJsonWriter,
            @NonNull final ExportDtoMappers mapper
    ) {
        log.log(Level.FINE, "Creating groupedByRecordReportGenerator");
        return new GroupedByRecordReportGenerator(customFormatWriter, prettyJsonWriter, mapper);
    }

    @NonNull
    @Bean
    public ExportDtoMappers exportDtoMappers(
            @NonNull final StopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper mapper
    ) {
        log.log(Level.FINE, "Creating exportDtoMappers");
        return new ExportDtoMappers(mapper);
    }

    @NonNull
    @Bean
    public CustomFormatWriter customFormatMapper(@NonNull final StopwatchMeasurementExportDtoCustomFormatWriter writer) {
        log.log(Level.FINE, "Creating customFormatMapper");
        return new CustomFormatWriter(writer);
    }
}
