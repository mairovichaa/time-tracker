package time_tracker.config.report;

import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.common.di.Bean;
import time_tracker.service.report.common.StopwatchMeasurementExportDtoCustomFormatWriter;
import time_tracker.service.report.common.StopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper;
import time_tracker.service.report.groupedByDate.CustomFormatWriter;
import time_tracker.service.report.groupedByDate.ExportDtoMappers;
import time_tracker.service.report.groupedByDate.GroupedByDateReportGenerator;

import java.util.logging.Level;

@Log
public class GroupedByDateConfiguration {

    @NonNull
    @Bean
    public GroupedByDateReportGenerator groupedByDateReportGenerator(
            @NonNull final CustomFormatWriter customFormatWriter,
            @NonNull final ObjectWriter prettyJsonWriter,
            @NonNull final ExportDtoMappers mapper
    ) {
        log.log(Level.FINE, "Creating groupedByDateReportGenerator");
        return new GroupedByDateReportGenerator(customFormatWriter, prettyJsonWriter, mapper);
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
        log.log(Level.FINE, "Creating exportDtoMappers");
        return new CustomFormatWriter(writer);
    }
}
