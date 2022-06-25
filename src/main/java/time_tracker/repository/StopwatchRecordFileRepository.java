package time_tracker.repository;

import lombok.RequiredArgsConstructor;
import time_tracker.annotation.NonNull;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static time_tracker.Constants.DATA_TIME_FORMATTER;

@RequiredArgsConstructor
public class StopwatchRecordFileRepository implements StopwatchRecordRepository {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy_MM_dd");
    public static final String FILENAME_PATTERN = "%s.txt";
    private final static String MEASUREMENT_PATTERN = "%s;%s;%s";
    private final static String DELIMITER = "---";

    @NonNull
    private final Path pathToDirWithData;

    @Override
    public void store(@NonNull final List<StopwatchRecord> records, @NonNull final LocalDate date) {
        var dateFormatted = DATE_FORMAT.format(date);
        var filename = String.format(FILENAME_PATTERN, dateFormatted);
        var pathToFile = pathToDirWithData.resolve(filename);
        List<String> data = new ArrayList<>();
        for (StopwatchRecord record : records) {
            data.add(record.getName());
            for (StopwatchRecordMeasurement measurement : record.getMeasurementsProperty()) {
                var startedAt = measurement.getStartedAt();
                var startedAtFormatted = DATA_TIME_FORMATTER.format(startedAt);
                var stoppedAt = measurement.getStoppedAt();
                var stoppedAtFormatted = DATA_TIME_FORMATTER.format(stoppedAt);
                var note = measurement.getNote();
                var measurementStr = String.format(MEASUREMENT_PATTERN, startedAtFormatted, stoppedAtFormatted, note);
                data.add(measurementStr);
            }
            data.add(DELIMITER);
        }
        try {
            Files.write(pathToFile, data, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.out.println("Can't store data " + e.getMessage());
        }
    }

}
