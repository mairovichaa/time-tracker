package time_tracker.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.NonNull;
import time_tracker.Utils;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DefaultStopwatchRecordService implements StopwatchRecordService {

    private final ObservableList<StopwatchRecord> stopwatchRecords = FXCollections.observableList(new ArrayList<>());

    @Override
    public ObservableList<StopwatchRecord> findAll() {
        return stopwatchRecords;
    }

    @Override
    public StopwatchRecord create(@NonNull final String name) {
        var record = new StopwatchRecord(name);
        stopwatchRecords.add(record);
        return record;
    }

    private final Map<String, Timer> recordToTimer = new HashMap<>();
    private final static DateTimeFormatter DATA_TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm:ss");

    private final static String TEXT_PATTERN = "%s -> %s = %s";
    private final static long TIMER_PERIOD_MILLIS = 1000;

    @Override
    public StopwatchRecordMeasurement startNewMeasurement(@NonNull StopwatchRecord record) {
        System.out.println("DefaultStopwatchRecordService#startNewMeasurement");
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

                var startedAt = measurement.getStartedAt();
                var formattedStartedAt = DATA_TIME_FORMATTER.format(startedAt);
                var formattedStoppedAt = DATA_TIME_FORMATTER.format(stoppedAt);

                var duration = measurement.getDuration();
                measurement.getDurationProperty().set(duration.getSeconds());
                var formattedDuration = Utils.formatDuration(duration);

                var text = String.format(TEXT_PATTERN, formattedStartedAt, formattedStoppedAt, formattedDuration);
                measurement.setMeasurementString(text);
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
        System.out.println("DefaultStopwatchRecordService#stopMeasurement");
        var timer = recordToTimer.get(record.getName());
        timer.cancel();

        var measurement = record.getMeasurementInProgress();
        var stoppedAt = LocalTime.now();
        measurement.setStoppedAt(stoppedAt);

        var startedAt = measurement.getStartedAt();
        var formattedStartedAt = DATA_TIME_FORMATTER.format(startedAt);
        var formattedStoppedAt = DATA_TIME_FORMATTER.format(stoppedAt);

        var duration = measurement.getDuration();
        measurement.getDurationProperty().set(duration.getSeconds());
        var formattedDuration = Utils.formatDuration(duration);

        var text = String.format(TEXT_PATTERN, formattedStartedAt, formattedStoppedAt, formattedDuration);
        measurement.setMeasurementString(text);

        record.setMeasurementInProgress(null);
        record.getHasMeasurementInProgress().set(false);
        record.getMeasurementsProperty().add(measurement);
    }
}
