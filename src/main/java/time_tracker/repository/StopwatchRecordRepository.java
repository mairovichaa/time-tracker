package time_tracker.repository;

import javafx.collections.ObservableList;
import time_tracker.annotation.NonNull;
import time_tracker.model.StopwatchRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StopwatchRecordRepository {
    void store(@NonNull final List<StopwatchRecord> records, @NonNull final LocalDate date);

    @NonNull
    List<StopwatchRecord> load(@NonNull final LocalDate date);

    @NonNull
    Map<LocalDate, List<StopwatchRecord>> load(@NonNull final LocalDate startDate, final int amountToLoad);

    Map<LocalDate, ObservableList<StopwatchRecord>> getLoaded();

}
