package time_tracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopwatchRecord;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Log
@RequiredArgsConstructor
public class StopwatchRecordOnLoadFactoryImpl implements StopwatchRecordOnLoadFactory {

    private final StopwatchProperties stopwatchProperties;

    @Override
    public List<StopwatchRecord> create() {
        var defaultRecords = stopwatchProperties.getDefaultRecords();
        log.fine(() -> "Create default on load records " + defaultRecords);
        return defaultRecords.stream()
                .map(StopwatchRecord::new)
                .collect(toList());
    }
}
