package time_tracker.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalQueries;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static time_tracker.Constants.DATA_TIME_FORMATTER;

@Log
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
        Path pathToFile = getPathToFile(date);
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
            log.severe(() -> "Can't write data to file: " + pathToFile);
            e.printStackTrace();
        }
    }

    @Override
    @NonNull
    public Map<LocalDate, List<StopwatchRecord>> load(@NonNull final LocalDate startDate, final int amountToLoad) {
        Map<LocalDate, List<StopwatchRecord>> result = new HashMap<>();

        for (int minusDays = 0; minusDays < amountToLoad; minusDays++) {
            var date = startDate.minusDays(minusDays);
            var loaded = this.load(date);
            result.put(date, loaded);
        }

        return result;
    }

    @Override
    @NonNull
    public List<StopwatchRecord> load(@NonNull final LocalDate date) {
        var pathToFile = getPathToFile(date);
        if (!Files.exists(pathToFile)) {
            return Collections.emptyList();
        }
        try {
            var result = new ArrayList<StopwatchRecord>();
            var data = Files.readAllLines(pathToFile)
                    .stream()
                    .map(String::strip)
                    .filter(Predicate.not(String::isBlank))
                    .collect(Collectors.toList());
            String name = null;
            List<StopwatchRecordMeasurement> measurements = new ArrayList<>();
            for (String line : data) {
                if (line.equals(DELIMITER)) {
                    var record = new StopwatchRecord();
                    record.setName(name);
                    record.setDate(date);

                    record.getMeasurementsProperty().addAll(measurements);
                    result.add(record);

                    name = null;
                    measurements.clear();
                    continue;
                }

                if (name == null) {
                    name = line;
                    continue;
                }

                var measurement = new StopwatchRecordMeasurement();
                var split = line.split(";");

                var startedAtStr = split[0];
                var startedAt = DATA_TIME_FORMATTER.parse(startedAtStr, TemporalQueries.localTime());
                measurement.setStartedAt(startedAt);

                var stoppedAtStr = split[1];
                var stoppedAt = DATA_TIME_FORMATTER.parse(stoppedAtStr, TemporalQueries.localTime());
                measurement.setStoppedAt(stoppedAt);

                if (split.length == 3) {
                    var note = split[2];
                    measurement.getNoteProperty().setValue(note);
                }

                measurements.add(measurement);
            }
            return result;

        } catch (IOException e) {
            log.severe(() -> "Can't load data from: " + pathToFile);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private Path getPathToFile(LocalDate date) {
        var dateFormatted = DATE_FORMAT.format(date);
        var filename = String.format(FILENAME_PATTERN, dateFormatted);
        return pathToDirWithData.resolve(filename);
    }

}
