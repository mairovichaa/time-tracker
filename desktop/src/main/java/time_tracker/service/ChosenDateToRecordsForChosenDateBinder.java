package time_tracker.service;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.model.DayData;
import time_tracker.model.StopWatchAppState;

import static time_tracker.service.DayDataService.DEFAULT_EXPECTED_TOTAL_IN_SECS;


@Log
@RequiredArgsConstructor
public class ChosenDateToRecordsForChosenDateBinder {

    private final StopWatchAppState appState;
    private final StopwatchRecordOnLoadFactory stopwatchRecordOnLoadFactory;

    public void bind() {
        appState.getChosenDateProperty()
                .addListener((observable, oldValue, chosenDate) -> {
                    log.finest("Chosen date is changed - update chosen records");
                    var records = appState.getDateToRecords().get(chosenDate);
                    if (records == null) {
                        log.fine("No records - use default on load factory");
                        var defaultOnLoadRecords = stopwatchRecordOnLoadFactory.create(chosenDate);
                        var newRecords = FXCollections.observableArrayList(defaultOnLoadRecords);
                        appState.getDateToRecords().put(chosenDate, newRecords);

                        var existingDayData = appState.getDateToDayData().get(chosenDate);
                        var dayData = new DayData(chosenDate, newRecords);
                        if (existingDayData == null) {
                            dayData.getExpectedTotalInSecsProperty()
                                    .setValue(DEFAULT_EXPECTED_TOTAL_IN_SECS);
                        } else {
                            dayData.setExpected(existingDayData.getExpected());
                            dayData.setNote(existingDayData.getNote());
                        }
                        appState.getDateToDayData().put(chosenDate, dayData);
                    }

                    Bindings.bindContent(appState.getRecordsForChosenDate(), appState.getDateToRecords().get(chosenDate));
                });
    }

}
