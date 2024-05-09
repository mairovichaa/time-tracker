package time_tracker.config.report;

import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.common.di.Bean;
import time_tracker.model.ReportState;
import time_tracker.model.StopWatchAppState;
import time_tracker.service.report.ReportExporter;
import time_tracker.service.report.ReportGenerator;
import time_tracker.controller.report.ReportController;
import time_tracker.service.report.groupedByDate.GroupedByDateReportGenerator;
import time_tracker.service.report.groupedByRecord.GroupedByRecordReportGenerator;

import java.util.logging.Level;

@Log
public class ReportConfiguration {

    @NonNull
    @Bean
    public ReportExporter reportExporter(
            @NonNull final ReportState reportState,
            @NonNull final ReportGenerator reportGenerator
    ) {
        return new ReportExporter(reportState, reportGenerator);
    }

    @NonNull
    @Bean(initMethod = "init")
    public ReportController reportController(@NonNull final ReportState reportState, @NonNull final StopWatchAppState stopWatchAppState) {
        log.log(Level.FINE, "Creating reportController");
        return new ReportController(reportState, stopWatchAppState);
    }

    @NonNull
    @Bean
    public ReportGenerator reportGenerator(@NonNull final GroupedByDateReportGenerator groupedByDateReportGenerator,
                                           @NonNull final GroupedByRecordReportGenerator groupedByRecordReportGenerator) {
        log.log(Level.FINE, "Creating reportGenerator");
        return new ReportGenerator(groupedByDateReportGenerator, groupedByRecordReportGenerator);
    }
}
