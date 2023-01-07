package time_tracker.temp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.domain.Record;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;
import time_tracker.model.mapper.StopwatchRecordMeasurementToMeasurementConverter;
import time_tracker.model.mapper.StopwatchRecordToRecordConverter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalQueries;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static time_tracker.Constants.DATA_TIME_FORMATTER;

@Log
public class FromCustomFormatToJsonConverter {

    private static final AtomicLong recordId = new AtomicLong(1);
    private static final AtomicLong measurementId = new AtomicLong(1);

    public static void main(String[] args) throws IOException {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        var stopwatchRecordMeasurementToMeasurementConverter = new StopwatchRecordMeasurementToMeasurementConverter();
        var stopwatchRecordToRecordConverter = new StopwatchRecordToRecordConverter(stopwatchRecordMeasurementToMeasurementConverter);

        var folder = Paths.get("/home/andrey/Documents/projects/time-tracker/src/main/resources/tmp");

        var result = new ArrayList<Record>();
        Files.list(folder)
                .map(p -> {
                    var fileName = p.getFileName();
                    var dateString = fileName.toString().split("\\.")[0];
                    var date = DATE_FORMAT.parse(dateString, TemporalQueries.localDate());

                    return load(date, p);
                })
                .map(stopwatchRecords -> stopwatchRecords.stream()
                        .map(stopwatchRecordToRecordConverter::convert).collect(Collectors.toList())
                )
                .forEach(result::addAll);

        result.forEach(record-> {
            record.setId(recordId.getAndIncrement());
            record.getMeasurements()
                    .forEach(measurement -> measurement.setId(measurementId.getAndIncrement()));
        });

        String pathForResult = "/home/andrey/Documents/projects/time-tracker/src/main/resources/data.json";
        var newFile = Paths.get(pathForResult);
        objectMapper.writeValue(newFile.toFile(), result);
    }

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy_MM_dd");
    public static final String FILENAME_PATTERN = "%s.txt";
    private final static String MEASUREMENT_PATTERN = "%s;%s;%s";
    private final static String DELIMITER = "---";

    public static List<StopwatchRecord> load(@NonNull final LocalDate date, @NonNull final Path path) {
        try {
            var result = new ArrayList<StopwatchRecord>();
            var data = Files.readAllLines(path)
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
                    measurement.setNote(note);
                }

                measurements.add(measurement);
            }
            return result;

        } catch (IOException e) {
            log.severe(() -> "Can't load data from: " + path);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
