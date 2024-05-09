package time_tracker.controller.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.model.ReportState;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log
public class ReportController {

    private final ReportState reportState;
    private final StopWatchAppState stopWatchAppState;

    public void init() {
        log.finest("init");

        reportState.getStartDateProperty()
                .addListener((observable, oldValue, newValue) -> {
                    log.finest(() -> "New start date: " + Utils.formatLocalDate(newValue));
                    updateReportState();
                });

        reportState.getEndDateProperty()
                .addListener((observable, oldValue, newValue) -> {
                    log.finest(() -> "New end date: " + Utils.formatLocalDate(newValue));
                    updateReportState();
                });

        reportState.getGroupByRecord()
                .addListener((observable, oldValue, newValue) -> {
                    log.finest(() -> "New group by record: " + newValue);
                    updateReportState();
                });

        updateReportState();
    }

    private void updateReportState() {
        if (reportState.getGroupByRecord().get()) {
            List<StopwatchRecord> records = stopWatchAppState.getDateToRecords()
                    .keySet()
                    .stream()
                    .filter(it ->
                            (reportState.getStartDate().isBefore(it) || reportState.getStartDate().isEqual(it)) && (it.isEqual(reportState.getEndDate()) || it.isBefore(reportState.getEndDate())))
                    .map(stopWatchAppState.getDateToRecords()::get)
                    .flatMap(Collection::stream)
                    .toList();

            Map<String, List<StopwatchRecord>> recordNameToRecords = records.stream()
                    .collect(Collectors.groupingBy(StopwatchRecord::getName));

            reportState.getRecordNameToRecordsProperty().setValue(recordNameToRecords);
            reportState.getDateToRecordsProperty().setValue(Collections.emptyMap());
        } else {
            Map<LocalDate, List<StopwatchRecord>> recordsSortedAndGroupedByDate = stopWatchAppState.getDateToRecords()
                    .keySet()
                    .stream()
                    .filter(it ->
                            (reportState.getStartDate().isBefore(it) || reportState.getStartDate().isEqual(it)) && (it.isEqual(reportState.getEndDate()) || it.isBefore(reportState.getEndDate())))
                    .map(stopWatchAppState.getDateToRecords()::get)
                    .filter(Predicate.not(List::isEmpty))
                    .collect(Collectors.toMap(l -> l.get(0).getDate(), it -> it));

            reportState.getDateToRecordsProperty().setValue(recordsSortedAndGroupedByDate);
            reportState.getRecordNameToRecordsProperty().setValue(Collections.emptyMap());
        }
    }
}
