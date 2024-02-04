package time_tracker.service;

import javafx.application.Platform;
import lombok.extern.java.Log;
import time_tracker.common.DebounceContext;
import time_tracker.common.GlobalContext;
import time_tracker.common.annotation.NonNull;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;
import time_tracker.model.StopwatchSearchState;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Log
public class StopwatchRecordSearchServiceImpl implements StopwatchRecordSearchService {

    // TODO move to configs
    private static final String LOG_PATTERN = "Chosen record name is changed from %s to %s";
    private final static Duration DEBOUNCE_DURATION_FOR_SEARCH = Duration.ofMillis(500);
    @NonNull
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    @NonNull
    private final DebounceContext debounceContext = new DebounceContext(scheduledExecutorService, DEBOUNCE_DURATION_FOR_SEARCH);

    @Override
    public void initialize(
            @NonNull final StopwatchSearchState stopwatchSearchState,
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final StopwatchRecordSearchService stopwatchRecordSearchService) {
        var searchStateSearch = stopwatchSearchState.getSearch();

        searchStateSearch.addListener((observable, oldValue, newSearchTerm) -> {
            log.fine(() -> "Search term has changed from " + oldValue + " to " + newSearchTerm);
            debounceContext.runWithDebounce(
                    () -> Platform.runLater(() -> {
                        var found = stopwatchSearchState.getFound();
                        if (newSearchTerm.isBlank()) {
                            found.clear();
                            List<StopwatchRecord> allExistingRecords = stopWatchAppState.getDateToRecords()
                                    .values()
                                    .stream()
                                    .flatMap(Collection::stream)
                                    .toList();
                            found.addAll(allExistingRecords);
                            return;
                        }
                        var foundNew = stopWatchAppState.getDateToRecords()
                                .values()
                                .stream()
                                .flatMap(Collection::stream)
                                .filter(it -> it.getName().contains(newSearchTerm) || searchTermInRecordMeasurements(newSearchTerm, it))
                                .toList();
                        log.finest(() -> "Found by term " + newSearchTerm + ": " + foundNew);

                        found.clear();
                        found.addAll(foundNew);
                    })
            );
        });

        stopwatchSearchState.getChosenRecordName()
                .addListener((observable, oldValue, newRecordName) -> {
                    log.fine(() -> String.format(LOG_PATTERN, oldValue, newRecordName));
                    var recordsForName = stopwatchRecordSearchService.recordsByName(newRecordName);
                    recordsForName.sort(comparing(StopwatchRecord::getDate).reversed());
                    stopwatchSearchState.getRecordsForChosenName().clear();
                    stopwatchSearchState.getRecordsForChosenName().addAll(recordsForName);
                });
    }

    @Override
    public void shutdown() {
        scheduledExecutorService.shutdown();
    }

    @Override
    @NonNull
    public List<StopwatchRecord> recordsByName(@NonNull final String name) {
        var appState = GlobalContext.get(StopWatchAppState.class);
        return appState.getDateToRecords()
                .values()
                .stream()
                .flatMap(List::stream)
                .filter(record -> record.getName().equals(name))
                .collect(toList());
    }

    private boolean searchTermInRecordMeasurements(String searchTerm, StopwatchRecord record) {
        return record.getMeasurementsProperty()
                .stream()
                .map(StopwatchRecordMeasurement::getNote)
                .filter(Objects::nonNull)
                .anyMatch(it -> it.contains(searchTerm));
    }
}
