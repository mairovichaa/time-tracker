package time_tracker.config.report;

import lombok.extern.java.Log;
import time_tracker.common.GlobalContext;
import time_tracker.common.annotation.NonNull;
import time_tracker.config.properties.AppProperties;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.ReportState;
import time_tracker.model.StopWatchAppState;
import time_tracker.service.report.ReportExporter;
import time_tracker.service.report.ReportGenerator;
import time_tracker.service.report.ReportService;
import time_tracker.service.report.common.StopwatchMeasurementExportDtoCustomFormatWriter;
import time_tracker.service.report.common.StopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper;
import time_tracker.service.report.groupedByDate.GroupedByDateReportGenerator;
import time_tracker.service.report.groupedByRecord.GroupedByRecordReportGenerator;

import java.time.LocalDate;
import java.util.logging.Level;

@Log
public class ReportConfiguration {
    @NonNull
    public ReportState reportState(final @NonNull AppProperties appProperties) {
        log.log(Level.FINE, "Creating reportState");
        var reportState = new ReportState();
        StopwatchProperties.ReportProperties report = appProperties.getStopwatch().getReport();
        LocalDate startDate = LocalDate.now().minusDays(report.getNumberOfDaysToShow());
        reportState.getStartDateProperty().setValue(startDate);
        reportState.getEndDateProperty().setValue(LocalDate.now());
        boolean showTime = report.isShowTime();
        reportState.getShowTimeProperty().setValue(showTime);
        boolean groupByReport = report.getGroupBy() == StopwatchProperties.ReportProperties.GroupBy.REPORT;
        reportState.getGroupByRecord().setValue(groupByReport);
        StopwatchProperties.ReportProperties.ExportFormat exportFormat = report.getExportFormat();
        reportState.getExportFormat().setValue(exportFormat);

        return GlobalContext.createStoreAndReturn(ReportState.class, () -> reportState);
    }

    @NonNull
    public ReportExporter reportExporter(
            @NonNull final ReportState reportState,
            @NonNull final ReportGenerator reportGenerator
    ) {
        return GlobalContext.createStoreAndReturn(ReportExporter.class, () -> new ReportExporter(reportState, reportGenerator));
    }

    @NonNull
    public ReportService reportService(@NonNull final ReportState reportState, @NonNull final StopWatchAppState stopWatchAppState) {
        log.log(Level.FINE, "Creating reportService");
        return GlobalContext.createStoreAndReturn(ReportService.class, () -> new ReportService(reportState, stopWatchAppState));
    }

    @NonNull
    public ReportGenerator reportGenerator(@NonNull final GroupedByDateReportGenerator groupedByDateReportGenerator,
                                           @NonNull final GroupedByRecordReportGenerator groupedByRecordReportGenerator) {
        log.log(Level.FINE, "Creating reportGenerator");
        return GlobalContext.createStoreAndReturn(ReportGenerator.class, () -> new ReportGenerator(groupedByDateReportGenerator, groupedByRecordReportGenerator));
    }

    @NonNull
    public StopwatchMeasurementExportDtoCustomFormatWriter stopwatchMeasurementExportDtoCustomFormatWriter(@NonNull final ReportState reportState) {
        log.log(Level.FINE, "Creating stopwatchMeasurementExportDtoCustomFormatWriter");
        return GlobalContext.createStoreAndReturn(StopwatchMeasurementExportDtoCustomFormatWriter.class, () -> new StopwatchMeasurementExportDtoCustomFormatWriter(reportState));
    }

    @NonNull
    public StopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper stopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper(@NonNull final ReportState reportState) {
        log.log(Level.FINE, "Creating stopwatchMeasurementExportDtoCustomFormatWriter");
        return GlobalContext.createStoreAndReturn(StopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper.class, StopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper::new);
    }
}
