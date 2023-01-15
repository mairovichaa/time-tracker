package time_tracker.model;

import javafx.beans.binding.BooleanBinding;
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
    private BooleanProperty tracked = new SimpleBooleanProperty(false);

    public boolean isTracked() {
        return tracked.get();
    }

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
    private final LongProperty trackedInSecsProperty = new SimpleLongProperty();

    @Getter
    private final LongProperty expectedTotalInSecsProperty = new SimpleLongProperty(-1);

    @Getter
    private final LongProperty timeToWorkLeftProperty = new SimpleLongProperty(expectedTotalInSecsProperty.get());

    public long getTimeToWorkLeft() {
        return timeToWorkLeftProperty.get();
    }

    @Getter
    private final LongProperty overtimeProperty = new SimpleLongProperty(0);

    public long getOvertime() {
        return overtimeProperty.get();
    }

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


        var trackedTimeInSecs = new LongBinding() {
            @NonNull
            private List<StopwatchRecord> bound = records;

            {
                records.stream()
                        .map(StopwatchRecord::getIsChangedProperty)
                        .forEach(super::bind);
            }

            @Override
            protected long computeValue() {
                return bound.stream()
                        .filter(it -> it.getTrackedProperty().get())
                        .mapToLong(it -> it.getMeasurementsTotalInSecsLongBinding().getValue())
                        .sum();
            }

            public void rebind() {
                log.fine(() -> "Rebind total time in secs");
                bound.stream()
                        .map(StopwatchRecord::getIsChangedProperty)
                        .forEach(super::unbind);
                records.stream()
                        .map(StopwatchRecord::getIsChangedProperty)
                        .forEach(super::bind);
                bound = records;
                this.invalidate();
            }
        };

        records.addListener((ListChangeListener<StopwatchRecord>) c -> {
            log.fine("List of stopwatch records changed - rebind total");
            measurementsTotalTimeInSecs.rebind();
            trackedTimeInSecs.rebind();
            this.amountOfRecordsProperty.setValue(records.size());
        });

        this.totalInSecsProperty.bind(measurementsTotalTimeInSecs);
        this.amountOfRecordsProperty.setValue(records.size());

        this.trackedInSecsProperty.bind(trackedTimeInSecs);
        this.tracked.bind(new BooleanBinding() {
            {
                bind(totalInSecsProperty, trackedInSecsProperty);
            }

            @Override
            protected boolean computeValue() {
                return totalInSecsProperty.get() - trackedInSecsProperty.get() <= 0;
            }
        });

        timeToWorkLeftProperty.bind(new LongBinding() {
            {
                bind(measurementsTotalTimeInSecs, expectedTotalInSecsProperty);
            }

            @Override
            protected long computeValue() {
                var diffInSecs = expectedTotalInSecsProperty.getValue() - measurementsTotalTimeInSecs.getValue();
                return Math.max(diffInSecs, 0);
            }
        });

        overtimeProperty.bind(new LongBinding() {
            {
                bind(measurementsTotalTimeInSecs, expectedTotalInSecsProperty);
            }
            @Override
            protected long computeValue() {
                var diffInSecs = expectedTotalInSecsProperty.getValue() - measurementsTotalTimeInSecs.getValue();
                return Math.abs(Math.min(diffInSecs, 0));
            }
        });

        this.tracked.addListener((observable, oldValue, newValue) -> log.fine("Listener to turn off lazy calculation"));
        this.trackedInSecsProperty.addListener((observable, oldValue, newValue) -> log.fine("Listener to turn off lazy calculation"));
        this.totalInSecsProperty.addListener((observable, oldValue, newValue) -> log.fine("Listener to turn off lazy calculation"));
    }
}
