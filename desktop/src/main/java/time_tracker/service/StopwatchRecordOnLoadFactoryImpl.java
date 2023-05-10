package time_tracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopwatchRecord;
import time_tracker.repository.StopwatchRecordRepository;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Log
@RequiredArgsConstructor
public class StopwatchRecordOnLoadFactoryImpl implements StopwatchRecordOnLoadFactory {
    private final StopwatchProperties stopwatchProperties;
    private final StopwatchRecordRepository stopwatchRecordRepository;

    @Override
    @NonNull
    public List<StopwatchRecord> create(@NonNull final LocalDate date) {
        var defaultRecords = stopwatchProperties.getDefaultRecords();
        log.fine(() -> "Create default on load records " + defaultRecords);
        return defaultRecords.stream()
                .map(it -> {
                    var record = new StopwatchRecord();
                    record.setId(stopwatchRecordRepository.nextIdForRecord());
                    record.setName(it);
                    record.setDate(date);
                    return record;
                })
                .collect(toList());
    }
}
