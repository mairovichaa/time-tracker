package time_tracker.service;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.model.StopWatchAppState;

@Log
@RequiredArgsConstructor
public class ChosenDateToRecordsForChosenDateBinder {

    private final StopWatchAppState appState;
    private final StopwatchRecordOnLoadFactory stopwatchRecordOnLoadFactory;

    public void bind() {
        appState.getChosenDateProperty()
                .addListener((observable, oldValue, chosenDate) -> {
                    var records = appState.getDateToRecords().get(chosenDate);
                    if (records == null) {
                        log.fine("No records - use default on load factory");
                        var defaultOnLoadRecords = stopwatchRecordOnLoadFactory.create(chosenDate);
                        var newRecords = FXCollections.observableArrayList(defaultOnLoadRecords);
                        appState.getDateToRecords().put(chosenDate, newRecords);
                    }

                    Bindings.bindContent(appState.getRecordsForChosenDate(), appState.getDateToRecords().get(chosenDate));
                });
    }

}
