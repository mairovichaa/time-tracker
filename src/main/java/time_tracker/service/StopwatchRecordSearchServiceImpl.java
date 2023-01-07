package time_tracker.service;

import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.config.StopwatchSearchState;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;

import java.util.Collection;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Log
public class StopwatchRecordSearchServiceImpl implements StopwatchRecordSearchService {

    @Override
    public void initialize(@NonNull final StopwatchSearchState stopwatchSearchState, @NonNull final StopWatchAppState stopWatchAppState) {
        var searchStateSearch = stopwatchSearchState.getSearch();

        searchStateSearch.addListener((observable, oldValue, newSearchTerm) -> {
            log.fine(() -> "Search term has changed from " + oldValue + " to " + newSearchTerm);
            var found = stopwatchSearchState.getFound();
            if (newSearchTerm.isBlank()) {
                found.clear();
                return;
            }
            var foundNew = stopWatchAppState.getDateToRecords()
                    .values()
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(it -> it.getName().contains(newSearchTerm) || searchTermInRecordMeasurements(newSearchTerm, it))
                    .collect(toList());
            log.finest(() -> "Found by term " + newSearchTerm + ": " + foundNew);

            found.clear();
            found.addAll(foundNew);
        });
    }

    private boolean searchTermInRecordMeasurements(String searchTerm, StopwatchRecord record) {
        return record.getMeasurementsProperty()
                .stream()
                .map(StopwatchRecordMeasurement::getNote)
                .filter(Objects::nonNull)
                .anyMatch(it -> it.contains(searchTerm));
    }
}
