package time_tracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;

import java.util.logging.Level;

@Log
@RequiredArgsConstructor
public class AppStateService {

    @NonNull
    private final StopwatchRecordService stopwatchRecordService;

    @NonNull
    private final StopWatchAppState appState;

    public void delete(@NonNull final StopwatchRecord record) {
        log.log(Level.FINE, () -> "delete record: " + record);
        stopwatchRecordService.delete(record);
        stopwatchRecordService.store(record);

        var searchState = appState.getSearchState();
        var recordsForChosenName = searchState.getRecordsForChosenName();
        recordsForChosenName.remove(record);

        if (recordsForChosenName.isEmpty()) {
            searchState.getChosenRecordName().setValue(null);

            var chosenSearchRecordName = record.getName();
            searchState.getFound()
                    .removeIf(it -> it.equals(chosenSearchRecordName));
        }
    }

    public void store() {
        log.log(Level.FINE, "store");
        stopwatchRecordService.store();
    }
}
