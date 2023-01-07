package time_tracker.model;

import javafx.beans.binding.LongBinding;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;

@Log
public class DayData {
    @Getter
    @Setter
    // TODO initialize
    private Long id;
    @Getter
    private final LocalDate date;

    @Getter
    private final LongProperty totalInSecsProperty = new SimpleLongProperty(0);

    public long getTotalInSecs() {
        return totalInSecsProperty.get();
    }

    @Getter
    private final IntegerProperty amountOfRecordsProperty = new SimpleIntegerProperty(0);

    public int getAmountOfRecords() {
        return amountOfRecordsProperty.get();
    }

    @Getter
    private final LongProperty expectedTotalInSecsProperty = new SimpleLongProperty(-1);

    public boolean isExpectedTotalInSecsInitialized() {
        return expectedTotalInSecsProperty.getValue() != -1;
    }

    @Getter
    private final StringProperty noteProperty = new SimpleStringProperty();

    public String getNote() {
        return noteProperty.getValue();
    }

    public void setNote(@NonNull String note) {
        noteProperty.setValue(note);
    }

    @Getter
    private final ObservableList<StopwatchRecord> records;

    public DayData(LocalDate date, ObservableList<StopwatchRecord> records) {
        this.date = date;
        this.records = records;

        var measurementsTotalTimeInSecs = new LongBinding() {
            @NonNull
            private List<LongBinding> bound = records.stream()
                    .map(StopwatchRecord::getMeasurementsTotalInSecsLongBinding)
                    .collect(toUnmodifiableList());

            {
                bound.forEach(super::bind);
            }

            @Override
            protected long computeValue() {
                return bound.stream()
                        .mapToLong(LongBinding::getValue)
                        .sum();
            }

            public void rebind() {
                log.fine(() -> "Rebind total time in secs");
                bound.forEach(super::unbind);
                var newBindings = records.stream()
                        .map(StopwatchRecord::getMeasurementsTotalInSecsLongBinding)
                        .collect(toUnmodifiableList());
                newBindings.forEach(super::bind);
                bound = newBindings;
                this.invalidate();
            }
        };

        records.addListener((ListChangeListener<StopwatchRecord>) c -> {
            log.fine("List of stopwatch records changed - rebind total");
            measurementsTotalTimeInSecs.rebind();
            this.amountOfRecordsProperty.setValue(records.size());
        });

        this.totalInSecsProperty.bind(measurementsTotalTimeInSecs);
        this.amountOfRecordsProperty.setValue(records.size());
    }
}
