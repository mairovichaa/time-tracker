package time_tracker.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.domain.Measurement;
import time_tracker.domain.Record;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Log
@RequiredArgsConstructor
public class StopwatchRecordFileRepository implements StopwatchRecordRepository {
    private final static String FILENAME = "records";

    @NonNull
    private final FileRepository fileRepository;

    private final AtomicLong nextRecordId = new AtomicLong();
    private final AtomicLong nextMeasurementId = new AtomicLong();

    @Override
    public void save(@NonNull final Collection<Record> records) {
        fileRepository.save(FILENAME, records);
    }

    @Override
    public List<Record> findAll() {
        var typeReference = new TypeReference<List<Record>>() {
        };
        List<Record> records = fileRepository.get(typeReference, FILENAME);

        var nextRecordIdLong = records.stream()
                .mapToLong(Record::getId)
                .max().orElse(0) + 1;
        nextRecordId.set(nextRecordIdLong);

        var nextMeasurementIdLong = records.stream()
                .map(Record::getMeasurements)
                .flatMap(Collection::stream)
                .mapToLong(Measurement::getId)
                .max().orElse(0) + 1;
        nextMeasurementId.set(nextMeasurementIdLong);

        return records;
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
