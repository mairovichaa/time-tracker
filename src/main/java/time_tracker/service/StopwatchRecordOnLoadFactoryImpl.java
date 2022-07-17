package time_tracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopwatchRecord;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Log
@RequiredArgsConstructor
public class StopwatchRecordOnLoadFactoryImpl implements StopwatchRecordOnLoadFactory {
    private final StopwatchProperties stopwatchProperties;

    @Override
    @NonNull
    public List<StopwatchRecord> create(@NonNull final LocalDate date) {
        var defaultRecords = stopwatchProperties.getDefaultRecords();
        log.fine(() -> "Create default on load records " + defaultRecords);
        return defaultRecords.stream()
                .map(it -> {
                    var record = new StopwatchRecord();
                    record.setName(it);
                    record.setDate(date);
                    return record;
                })
                .collect(toList());
    }
}
