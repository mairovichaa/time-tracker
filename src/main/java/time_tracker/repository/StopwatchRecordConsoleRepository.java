package time_tracker.repository;

import time_tracker.annotation.NonNull;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;

import java.time.LocalDate;
import java.util.List;

import static time_tracker.Constants.DATA_TIME_FORMATTER;

public class StopwatchRecordConsoleRepository implements StopwatchRecordRepository {

    private final static String MEASUREMENT_PATTERN = "%s;%s;%s%n";

    @Override
    public void store(@NonNull final List<StopwatchRecord> records, @NonNull final LocalDate date) {
        for (StopwatchRecord record : records) {
            System.out.println(record.getName());
            for (StopwatchRecordMeasurement measurement : record.getMeasurementsProperty()) {
                var startedAt = measurement.getStartedAt();
                var startedAtFormatted = DATA_TIME_FORMATTER.format(startedAt);
                var stoppedAt = measurement.getStoppedAt();
                var stoppedAtFormatted = DATA_TIME_FORMATTER.format(stoppedAt);
                var note = measurement.getNote();
                System.out.printf(MEASUREMENT_PATTERN, startedAtFormatted, stoppedAtFormatted, note);
            }
            System.out.println("---");
        }
    }

}
