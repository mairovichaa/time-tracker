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
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
            runSearch(stopwatchSearchState, stopWatchAppState);
        });

        stopwatchSearchState.getTracked().addListener((observable, oldValue, newValue) -> {
            log.fine(() -> "Tracked has changed from " + oldValue + " to " + newValue);
            runSearch(stopwatchSearchState, stopWatchAppState);
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

    private void runSearch(final StopwatchSearchState stopwatchSearchState, final StopWatchAppState stopWatchAppState) {
        String newSearchTerm = stopWatchAppState.getSearchState().getSearch().getValue();
        debounceContext.runWithDebounce(
                () -> Platform.runLater(() -> {
                    var found = stopwatchSearchState.getFound();
                    if (newSearchTerm.isBlank()) {
                        found.clear();
                        List<String> allExistingRecords = stopWatchAppState.getDateToRecords()
                                .values()
                                .stream()
                                .flatMap(Collection::stream)
                                .filter(it -> it.getMeasurementsTotalInSecs() > 0)
                                .collect(Collectors.groupingBy(StopwatchRecord::getName))
                                .entrySet()
                                .stream()
                                .filter(it -> filterByTracked(it, stopwatchSearchState))
                                .map(Map.Entry::getKey)
                                .toList();
                        found.addAll(allExistingRecords);
                        return;
                    }
                    List<String> foundNew = stopWatchAppState.getDateToRecords()
                            .values()
                            .stream()
                            .flatMap(Collection::stream)
                            .filter(it -> it.getMeasurementsTotalInSecs() > 0)
                            .collect(Collectors.groupingBy(StopwatchRecord::getName))
                            .entrySet()
                            .stream()
                            .filter(it -> filterByTerm(it.getValue(), newSearchTerm))
                            .filter(it -> filterByTracked(it, stopwatchSearchState))
                            .map(Map.Entry::getKey)
                            .toList();
                    log.finest(() -> "Found by term " + newSearchTerm + ": " + foundNew);

                    found.clear();
                    found.addAll(foundNew);
                })
        );
    }

    private boolean filterByTerm(
            @NonNull final List<StopwatchRecord> records,
            @NonNull final String searchTerm) {
        return records.stream()
                .anyMatch(it -> it.getName().toLowerCase().contains(searchTerm.toLowerCase()) || searchTermInRecordMeasurements(searchTerm, it));
    }

    private boolean filterByTracked(
            @NonNull final Map.Entry<String, List<StopwatchRecord>> it,
            @NonNull final StopwatchSearchState stopwatchSearchState) {
        Boolean trackedValueForSearch = stopwatchSearchState.getTracked().getValue();
        if (trackedValueForSearch == null) {
            return true;
        }
        if (trackedValueForSearch == Boolean.TRUE) {
            return it.getValue().stream().allMatch(StopwatchRecord::isTracked);
        } else {
            return it.getValue().stream().anyMatch(Predicate.not(StopwatchRecord::isTracked));
        }
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
                .anyMatch(it -> it.toLowerCase().contains(searchTerm.toLowerCase()));
    }
}
