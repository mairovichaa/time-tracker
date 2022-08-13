package time_tracker.model;

import javafx.beans.binding.LongBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;

@Log
public class DayData {

    @Getter
    private final LocalDate date;

    @Getter
    private final LongProperty totalInSecs = new SimpleLongProperty();

    @Getter
    private final IntegerProperty amount = new SimpleIntegerProperty();

    public DayData(LocalDate date, ObservableList<StopwatchRecord> records) {
        this.date = date;

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
            this.amount.setValue(records.size());
        });

        this.totalInSecs.bind(measurementsTotalTimeInSecs);
        this.amount.setValue(records.size());
    }
}
