package time_tracker.service;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.annotation.NonNull;
import time_tracker.config.GlobalContext;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;
import time_tracker.repository.StopwatchRecordRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Level;

@RequiredArgsConstructor
@Log
public class DefaultStopwatchRecordService implements StopwatchRecordService {

    @NonNull
    private final StopWatchAppState stopWatchAppState;

    @NonNull
    private final StopwatchRecordRepository stopwatchRecordRepository;

    @NonNull
    private final StopwatchRecordOnLoadFactory stopwatchRecordOnLoadFactory;

    @Override
    public StopwatchRecord create(@NonNull final String name) {
        var record = new StopwatchRecord();
        record.setId(stopwatchRecordRepository.nextIdForRecord());
        record.setName(name);
        var chosenDate = stopWatchAppState.getChosenDate();
        record.setDate(chosenDate);

        stopWatchAppState.getDateToRecords()
                .get(chosenDate)
                .add(record);
        return record;
    }

    private final Map<String, Timer> recordToTimer = new HashMap<>();
    private final static long TIMER_PERIOD_MILLIS = 1000;

    @Override
    public StopwatchRecordMeasurement startNewMeasurement(@NonNull StopwatchRecord record) {
        log.log(Level.FINE, () -> "start new measurement " + record);
        var now = LocalTime.now();
        var measurement = new StopwatchRecordMeasurement();
        measurement.setId(stopwatchRecordRepository.nextIdForMeasurement());
        measurement.setStartedAt(now);
        measurement.setStoppedAt(now);

        record.setMeasurementInProgress(measurement);

        var timer = new Timer();
        recordToTimer.put(record.getName(), timer);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                var stoppedAt = LocalTime.now();
                Platform.runLater(() -> measurement.setStoppedAt(stoppedAt));
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
        record.getMeasurementsProperty().add(measurement);
    }

    @Override
    public void store() {
        var date = stopWatchAppState.getChosenDate();
        store(date);
    }

    @Override
    public void store(StopwatchRecord record) {
        store(record.getDate());
    }

    @Override
    public void store(@NonNull final LocalDate date) {
        ObservableList<StopwatchRecord> records = stopWatchAppState.getDateToRecords()
                .computeIfAbsent(date, ignored -> {
                    var defaultRecords = stopwatchRecordOnLoadFactory.create(date);
                    return FXCollections.observableArrayList(defaultRecords);
                });

        var copiedRecords = new ArrayList<>(records);
        stopwatchRecordRepository.store(copiedRecords, date);
    }

    @Override
    public void delete(StopwatchRecord record) {
        log.log(Level.FINE, () -> "delete record: " + record);
        var removed = stopWatchAppState.getDateToRecords()
                .get(record.getDate())
                .remove(record);
        if (!removed) {
            log.log(Level.SEVERE, () -> "Record can't be removed as it hasn't been found: " + record);
        }
    }

    @Override
    public void moveToDate(@NonNull final StopwatchRecord record, @NonNull final LocalDate newDate) {
        var oldDate = record.getDate();
        stopWatchAppState.getDateToRecords()
                .get(oldDate)
                .remove(record);

        record.setDate(newDate);
        var stopwatchRecords = stopWatchAppState.getDateToRecords()
                .get(newDate);
        if (stopwatchRecords == null) {
            log.fine(() -> "There is no data for " + Utils.formatLocalDate(newDate) + " date");
            var dayDataService = GlobalContext.get(DayDataService.class);
            dayDataService.create(newDate);
            stopwatchRecords = stopWatchAppState.getDateToRecords()
                    .get(newDate);
        }
        stopwatchRecords.add(record);

        this.store(oldDate);
        this.store(newDate);
    }
}
