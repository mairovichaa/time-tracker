package time_tracker.service;

import javafx.application.Platform;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.config.StopwatchSearchState;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;

import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

@Log
public class StopwatchRecordSearchServiceImpl implements StopwatchRecordSearchService {

    // TODO move to configs
    private final static Duration DEBOUNCE_DURATION_FOR_SEARCH = Duration.ofMillis(500);

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private volatile ScheduledFuture<?> currentScheduledFuture;
    @Override
    public void initialize(@NonNull final StopwatchSearchState stopwatchSearchState, @NonNull final StopWatchAppState stopWatchAppState) {
        var searchStateSearch = stopwatchSearchState.getSearch();

        searchStateSearch.addListener((observable, oldValue, newSearchTerm) -> {
            log.fine(() -> "Search term has changed from " + oldValue + " to " + newSearchTerm);
            if (currentScheduledFuture != null){
                log.fine(() -> "Cancel scheduled search  ");
                currentScheduledFuture.cancel(true);
            }

            currentScheduledFuture = scheduledExecutorService.schedule(
                    () -> {
                        currentScheduledFuture = null;
                        Platform.runLater(() -> {
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
                    },
                    DEBOUNCE_DURATION_FOR_SEARCH.getNano(),
                    TimeUnit.NANOSECONDS);
        });
    }

    @Override
    public void shutdown() {
        scheduledExecutorService.shutdown();
    }

    private boolean searchTermInRecordMeasurements(String searchTerm, StopwatchRecord record) {
        return record.getMeasurementsProperty()
                .stream()
                .map(StopwatchRecordMeasurement::getNote)
                .filter(Objects::nonNull)
                .anyMatch(it -> it.contains(searchTerm));
    }
}
