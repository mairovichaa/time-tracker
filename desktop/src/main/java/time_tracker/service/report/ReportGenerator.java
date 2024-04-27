package time_tracker.service.report;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.model.ReportState;
import time_tracker.service.report.groupedByDate.GroupedByDateReportGenerator;
import time_tracker.service.report.groupedByRecord.GroupedByRecordReportGenerator;

@Log
@RequiredArgsConstructor
public class ReportGenerator {

    private final GroupedByDateReportGenerator groupedByDateReportGenerator;
    private final GroupedByRecordReportGenerator groupedByRecordReportGenerator;

    @NonNull
    public String generate(@NonNull final ReportState reportState) {
        if (reportState.isGroupByRecord()) {
            return groupedByRecordReportGenerator.generate(reportState);
        } else {
            return groupedByDateReportGenerator.generate(reportState);
        }
    }
}
