package time_tracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.config.StopwatchSearchState;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopwatchRecord;
import time_tracker.repository.StopwatchRecordRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Log
@RequiredArgsConstructor
public class StopwatchRecordSearchServiceImpl implements StopwatchRecordSearchService {

    @NonNull
    private final StopwatchRecordRepository stopwatchRecordRepository;
    @NonNull
    private final LocalDate initialDate;

    @NonNull
    private final StopwatchProperties stopwatchProperties;


    @Override
    public void initialize(@NonNull final StopwatchSearchState stopwatchSearchState) {
        var searchStateSearch = stopwatchSearchState.getSearch();
        var amountOfDaysToShow = stopwatchProperties.getDates().getAmountOfDaysToShow();

        searchStateSearch.addListener((observable, oldValue, newSearchTerm) -> {
            log.fine(() -> "Search term has changed from " + oldValue + " to " + newSearchTerm);
            var found = stopwatchSearchState.getFound();
            if (newSearchTerm.isBlank()) {
                found.clear();
                return;
            }
            var foundNew = stopwatchRecordRepository.load(initialDate, amountOfDaysToShow)
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
                .map(it -> it.getNoteProperty().getValue())
                .filter(Objects::nonNull)
                .anyMatch(it -> it.contains(searchTerm));
    }
}
