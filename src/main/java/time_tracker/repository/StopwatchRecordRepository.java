package time_tracker.repository;

import time_tracker.annotation.NonNull;
import time_tracker.model.StopwatchRecord;

import java.time.LocalDate;
import java.util.List;

public interface StopwatchRecordRepository {
    void store(@NonNull final List<StopwatchRecord> records, @NonNull final LocalDate date);

    @NonNull
    List<StopwatchRecord> load(@NonNull final LocalDate date);

}