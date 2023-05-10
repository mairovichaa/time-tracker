package time_tracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;

import java.util.List;

@Log
@RequiredArgsConstructor
public class DefaultStopwatchMeasurementService implements StopwatchMeasurementService {

    @NonNull
    private final StopWatchAppState stopWatchAppState;

    @Override
    public void delete(@NonNull Long id) {
        var allRecords = stopWatchAppState.getDateToRecords()
                .values();
        for (List<StopwatchRecord> records : allRecords) {
            for (StopwatchRecord record : records) {
                var measurements = record.getMeasurementsProperty();

                if (measurements.removeIf(measurement -> measurement.getId().equals(id))) {
                    log.fine("Remove measurement. id = " + id);
                    return;
                }
            }
        }
    }
}
