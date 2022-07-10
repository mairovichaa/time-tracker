package time_tracker.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;
import time_tracker.repository.StopwatchRecordRepository;

import java.time.LocalTime;
import java.util.*;
import java.util.logging.Level;

@RequiredArgsConstructor
@Log
public class DefaultStopwatchRecordService implements StopwatchRecordService {

    @NonNull
    private final StopWatchAppState stopWatchAppState;
    @NonNull
    private final ObservableList<StopwatchRecord> stopwatchRecords = FXCollections.observableList(new ArrayList<>());

    @NonNull
    private final StopwatchRecordRepository stopwatchRecordRepository;

    @Override
    public ObservableList<StopwatchRecord> findAll() {
        return stopwatchRecords;
    }

    @Override
    public void setRecords(List<StopwatchRecord> records) {
        stopwatchRecords.clear();
        stopwatchRecords.addAll(records);
    }

    @Override
    public StopwatchRecord create(@NonNull final String name) {
        var record = new StopwatchRecord(name);
        stopwatchRecords.add(record);
        return record;
    }

    private final Map<String, Timer> recordToTimer = new HashMap<>();
    private final static long TIMER_PERIOD_MILLIS = 1000;

    @Override
    public StopwatchRecordMeasurement startNewMeasurement(@NonNull StopwatchRecord record) {
        log.log(Level.FINE, () -> "start new measurement " + record);
        var now = LocalTime.now();
        var measurement = new StopwatchRecordMeasurement();
        measurement.setStartedAt(now);
        measurement.setStoppedAt(now);

        record.setMeasurementInProgress(measurement);
        record.getHasMeasurementInProgress().set(true);

        var timer = new Timer();
        recordToTimer.put(record.getName(), timer);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                var stoppedAt = LocalTime.now();
                measurement.setStoppedAt(stoppedAt);
            }
        };

        timer.scheduleAtFixedRate(
                timerTask,
                0,
                TIMER_PERIOD_MILLIS
        );

        return measurement;
    }

    @Override
    public void stopMeasurement(@NonNull StopwatchRecord record) {
        log.log(Level.FINE, () -> "stop measurement: " + record);
        var timer = recordToTimer.get(record.getName());
        timer.cancel();

        var measurement = record.getMeasurementInProgress();
        var stoppedAt = LocalTime.now();
        measurement.setStoppedAt(stoppedAt);

        record.setMeasurementInProgress(null);
        record.getHasMeasurementInProgress().set(false);
        record.getMeasurementsProperty().add(measurement);
    }

    @Override
    public void store() {
        var date = stopWatchAppState.getChosenDate();
        stopwatchRecordRepository.store(stopwatchRecords, date);
    }
}
