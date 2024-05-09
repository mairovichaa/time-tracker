package time_tracker.service;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.common.annotation.NonNull;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;
import time_tracker.service.converter.RecordToStopwatchRecordConverter;
import time_tracker.service.converter.StopwatchRecordToRecordConverter;
import time_tracker.repository.StopwatchRecordRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static time_tracker.TimeTrackerApp.CONTEXT;

@RequiredArgsConstructor
@Log
public class DefaultStopwatchRecordService implements StopwatchRecordService {

    @NonNull
    private final StopWatchAppState stopWatchAppState;
    @NonNull
    private final StopwatchRecordRepository stopwatchRecordRepository;
    @NonNull
    private final StopwatchRecordOnLoadFactory stopwatchRecordOnLoadFactory;
    @NonNull
    private final StopwatchRecordToRecordConverter stopwatchRecordToRecordConverter;
    @NonNull
    private final RecordToStopwatchRecordConverter recordToStopwatchRecordConverter;

    @Getter
    private final Map<LocalDate, List<StopwatchRecord>> loaded = new HashMap<>();

    public void loadAll() {
        var records = stopwatchRecordRepository.findAll();
        var loadedTmp = recordToStopwatchRecordConverter.convert(records)
                .stream()
                .collect(Collectors.groupingBy(StopwatchRecord::getDate, Collectors.toList()));
        loadedTmp.keySet()
                .forEach(date -> {
                    List<StopwatchRecord> stopwatchRecords = loadedTmp.get(date);
                    loaded.put(date, stopwatchRecords);
                });
    }

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
        this.store(copiedRecords, date);
    }

    @Override
    public void delete(StopwatchRecord record) {
        log.log(Level.FINE, () -> "delete record: " + record);
        if (stopWatchAppState.getChosenStopwatchRecord().getValue() == record) {
            log.log(Level.FINE, () -> "delete chosen record - reset it");
            stopWatchAppState.resetChosenStopwatchRecord();
        }

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
            // TODO get rid of this
            var dayDataService = CONTEXT.get(DayDataService.class);
            dayDataService.create(newDate);
            stopwatchRecords = stopWatchAppState.getDateToRecords()
                    .get(newDate);
        }
        stopwatchRecords.add(record);

        this.store(oldDate);
        this.store(newDate);
    }

    private void store(@NonNull final List<StopwatchRecord> records, @NonNull final LocalDate date) {
        var stopwatchRecords = loaded.computeIfAbsent(date, ignored -> {
            log.severe(() -> "No data for: " + date);

            return new ArrayList<>();
        });
        stopwatchRecords.clear();
        stopwatchRecords.addAll(records);

        var result = stopwatchRecordToRecordConverter.convert(loaded.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList())
        );

        stopwatchRecordRepository.save(result);
    }
}
