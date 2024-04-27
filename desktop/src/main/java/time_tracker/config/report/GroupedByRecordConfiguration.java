package time_tracker.config.report;

import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.java.Log;
import time_tracker.common.GlobalContext;
import time_tracker.common.annotation.NonNull;
import time_tracker.service.report.common.StopwatchMeasurementExportDtoCustomFormatWriter;
import time_tracker.service.report.common.StopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper;
import time_tracker.service.report.groupedByRecord.CustomFormatWriter;
import time_tracker.service.report.groupedByRecord.ExportDtoMappers;
import time_tracker.service.report.groupedByRecord.GroupedByRecordReportGenerator;

import java.util.logging.Level;

@Log
public class GroupedByRecordConfiguration {

    @NonNull
    public GroupedByRecordReportGenerator groupedByRecordReportGenerator(
            @NonNull final CustomFormatWriter customFormatWriter,
            @NonNull final ObjectWriter prettyJsonWriter,
            @NonNull final ExportDtoMappers mapper
    ) {
        log.log(Level.FINE, "Creating groupedByRecordReportGenerator");
        return GlobalContext.createStoreAndReturn(GroupedByRecordReportGenerator.class, () -> new GroupedByRecordReportGenerator(customFormatWriter, prettyJsonWriter, mapper));
    }

    @NonNull
    public ExportDtoMappers exportDtoMappers(
            @NonNull final StopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper mapper
    ) {
        log.log(Level.FINE, "Creating exportDtoMappers");
        return GlobalContext.createStoreAndReturn(ExportDtoMappers.class, () -> new ExportDtoMappers(mapper));
    }

    @NonNull
    public CustomFormatWriter customFormatMapper(@NonNull final StopwatchMeasurementExportDtoCustomFormatWriter writer) {
        log.log(Level.FINE, "Creating customFormatMapper");
        return GlobalContext.createStoreAndReturn(CustomFormatWriter.class, () -> new CustomFormatWriter(writer));
    }
}
