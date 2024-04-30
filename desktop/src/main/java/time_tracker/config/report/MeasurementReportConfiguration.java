package time_tracker.config.report;

import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.common.di.Bean;
import time_tracker.config.properties.AppProperties;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.ReportState;
import time_tracker.service.report.common.StopwatchMeasurementExportDtoCustomFormatWriter;
import time_tracker.service.report.common.StopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper;

import java.time.LocalDate;
import java.util.logging.Level;

@Log
public class MeasurementReportConfiguration {
    @NonNull
    @Bean
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

        return reportState;
    }

    @NonNull
    @Bean
    public StopwatchMeasurementExportDtoCustomFormatWriter stopwatchMeasurementExportDtoCustomFormatWriter(@NonNull final ReportState reportState) {
        log.log(Level.FINE, "Creating stopwatchMeasurementExportDtoCustomFormatWriter");
        return new StopwatchMeasurementExportDtoCustomFormatWriter(reportState);
    }

    @NonNull
    @Bean
    public StopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper stopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper() {
        log.log(Level.FINE, "Creating stopwatchMeasurementExportDtoCustomFormatWriter");
        return new StopwatchRecordMeasurementToStopwatchMeasurementExportDtoMapper();
    }
}
