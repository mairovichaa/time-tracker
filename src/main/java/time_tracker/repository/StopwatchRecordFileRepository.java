package time_tracker.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.domain.Record;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;
import time_tracker.model.mapper.RecordToStopwatchRecordConverter;
import time_tracker.model.mapper.StopwatchRecordToRecordConverter;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Log
public class StopwatchRecordFileRepository implements StopwatchRecordRepository {


    private final StopwatchRecordToRecordConverter stopwatchRecordToRecordConverter;
    private final RecordToStopwatchRecordConverter recordToStopwatchRecordConverter;
    private final ObjectMapper mapper;
    private final Path dataFile;
    private final Path tmpDataFile;

    private final AtomicLong nextRecordId = new AtomicLong();
    private final AtomicLong nextMeasurementId = new AtomicLong();

    @Getter
    // TODO get rid of Observable list - pass usual list
    // now there is a bound via data between AppState and this class - it should be removed
    // see time_tracker.model.StopWatchAppState
    private Map<LocalDate, ObservableList<StopwatchRecord>> loaded = new HashMap<>();

    public StopwatchRecordFileRepository(
            @NonNull final Path pathToDirWithData,
            @NonNull final ObjectMapper mapper,
            @NonNull final StopwatchRecordToRecordConverter stopwatchRecordToRecordConverter,
            @NonNull final RecordToStopwatchRecordConverter recordToStopwatchRecordConverter
    ) {
        dataFile = pathToDirWithData.resolve("data.json");
        tmpDataFile = pathToDirWithData.resolve("data-tmp.json");
        this.stopwatchRecordToRecordConverter = stopwatchRecordToRecordConverter;
        this.recordToStopwatchRecordConverter = recordToStopwatchRecordConverter;
        this.mapper = mapper;
    }

    public void loadAll() {
        var recordTypeReference = new TypeReference<List<Record>>() {
        };
        try {
            var records = mapper.readValue(dataFile.toFile(), recordTypeReference);
            var loadedTmp = recordToStopwatchRecordConverter.convert(records)
                    .stream()
                    .collect(Collectors.groupingBy(StopwatchRecord::getDate, Collectors.toList()));

            loadedTmp.keySet()
                    .forEach(date -> {
                        var stopwatchRecords = loadedTmp.get(date);
                        loaded.put(date, FXCollections.observableArrayList(stopwatchRecords));
                    });

            var nextRecordIdLong = loaded.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .mapToLong(StopwatchRecord::getId)
                    .max().orElse(0) + 1;
            nextRecordId.set(nextRecordIdLong);

            var nextMeasurementIdLong = loaded.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .map(StopwatchRecord::getMeasurementsProperty)
                    .flatMap(Collection::stream)
                    .mapToLong(StopwatchRecordMeasurement::getId)
                    .max().orElse(0) + 1;
            nextMeasurementId.set(nextMeasurementIdLong);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void store(@NonNull final List<StopwatchRecord> records, @NonNull final LocalDate date) {
        var stopwatchRecords = loaded.computeIfAbsent(date, ignored -> {
            log.severe(() -> "No data for: " + date);

            return FXCollections.observableArrayList(new ArrayList<>());
        });
        stopwatchRecords.clear();
        stopwatchRecords.addAll(records);

        var result = stopwatchRecordToRecordConverter.convert(loaded.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList())
        );

        try {
            log.finest(() -> "Write data to tmp file in case of failure of write to data file - to keep original data file unchanged.");
            mapper.writeValue(tmpDataFile.toFile(), result);
        } catch (IOException e) {
            log.severe(() -> "Can't write to temp file: " + tmpDataFile);
            throw new RuntimeException(e);
        }

        try {
            mapper.writeValue(dataFile.toFile(), result);
        } catch (IOException e) {
            log.severe(() -> "Can't write to data file: " + dataFile);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long nextIdForRecord() {
        return nextRecordId.getAndIncrement();
    }

    @Override
    public Long nextIdForMeasurement() {
        return nextMeasurementId.getAndIncrement();
    }

}
