package time_tracker.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.NonNull;
import time_tracker.model.StopwatchRecord;

import java.util.ArrayList;

public class DefaultStopwatchRecordService implements StopwatchRecordService {

    private final ObservableList<StopwatchRecord> stopwatchRecords = FXCollections.observableList(new ArrayList<>());

    @Override
    public ObservableList<StopwatchRecord> findAll() {
        return stopwatchRecords;
    }

    @Override
    public StopwatchRecord create(@NonNull final String name) {
        return StopwatchRecord.builder()
                .name(name)
                .measurements(new ArrayList<>())
                .build();
    }
}
