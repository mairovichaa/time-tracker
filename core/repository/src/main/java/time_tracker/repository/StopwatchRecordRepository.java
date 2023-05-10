package time_tracker.repository;


import time_tracker.common.annotation.NonNull;
import time_tracker.domain.Record;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface StopwatchRecordRepository {
    void save(@NonNull final Collection<Record> records);

    @NonNull
    List<Record> findAll();

    Long nextIdForRecord();

    Long nextIdForMeasurement();

}
