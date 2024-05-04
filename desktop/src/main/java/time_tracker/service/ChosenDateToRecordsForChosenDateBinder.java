package time_tracker.service;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.DayData;
import time_tracker.model.StopWatchAppState;

import java.time.Duration;


@Log
@RequiredArgsConstructor
public class ChosenDateToRecordsForChosenDateBinder {

    private final StopWatchAppState appState;
    private final StopwatchProperties.DefaultDayStatisticProperties defaultDayStatisticProperties;
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
                        // TODO move creation of DayData to DayDataService
                        var dayData = new DayData(chosenDate, newRecords);
                        if (existingDayData == null) {
                            String duration = defaultDayStatisticProperties.getExpectedWorkTime();
                            String comment = defaultDayStatisticProperties.getComment();
                            Duration durationParsed = Duration.parse("PT" + duration);
                            dayData.setExpected(durationParsed);
                            dayData.setNote(comment);
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
