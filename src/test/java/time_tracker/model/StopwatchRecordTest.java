package time_tracker.model;

import javafx.collections.ListChangeListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StopwatchRecordTest {

    @Test
    public void shouldNotifyHasMeasurementInProgressWhenMeasurementInProgressIsSet() {
        // given
        var record = new StopwatchRecord();

        var isNotified = new AtomicBoolean();
        record.getHasMeasurementInProgressProperty()
                .addListener(observable -> isNotified.set(true));

        // when
        record.setMeasurementInProgress(new StopwatchRecordMeasurement());

        // then
        assertThat(isNotified.get()).isTrue();
    }

    @Test
    public void shouldNotifyHasMeasurementInProgressWhenMeasurementInProgressIsRemoved() {
        // given
        var record = new StopwatchRecord();
        record.setMeasurementInProgress(new StopwatchRecordMeasurement());

        var amountOfNotifications = new AtomicInteger();
        record.getHasMeasurementInProgressProperty()
                .addListener(observable -> amountOfNotifications.getAndIncrement());

        // when
        record.setMeasurementInProgress(null);

        // then
        assertThat(amountOfNotifications.get()).isEqualTo(1);
    }

    @Test
    public void shouldNotifyMeasurementsPropertyWhenMeasurementIsAdded() {
        // given
        var record = new StopwatchRecord();

        var amountOfNotifications = new AtomicInteger();
        record.getMeasurementsProperty()
                .addListener((ListChangeListener<? super StopwatchRecordMeasurement>) listener -> amountOfNotifications.getAndIncrement());

        // when
        record.getMeasurementsProperty()
                .add(new StopwatchRecordMeasurement());

        // then
        assertThat(amountOfNotifications.get()).isEqualTo(1);
    }

    @Test
    public void shouldNotifyMeasurementsPropertyWhenMeasurementIsRemoved() {
        // given
        var record = new StopwatchRecord();
        var measurement = new StopwatchRecordMeasurement();
        record.getMeasurementsProperty().add(measurement);

        var amountOfNotifications = new AtomicInteger();
        record.getMeasurementsProperty()
                .addListener((ListChangeListener<? super StopwatchRecordMeasurement>) listener -> amountOfNotifications.getAndIncrement());

        // when
        record.getMeasurementsProperty()
                .remove(measurement);

        // then
        assertThat(amountOfNotifications.get()).isEqualTo(1);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("shouldUpdateMeasurementsTotalInSecsLongBindingWhenMeasurementsOrMeasurementInProgressIsChangedMethodSource")
    void shouldUpdateMeasurementsTotalInSecsLongBindingWhenMeasurementsOrMeasurementInProgressIsChanged(
            String description,
            Consumer<StopwatchRecord> measurementRecordChange,
            int expectedTotal,
            int expectedAmountOfNotifications
    ) {
        // given
        var record = new StopwatchRecord();

        var amountOfNotifications = new AtomicInteger();
        record.getMeasurementsTotalInSecsLongBinding()
                .addListener((observable, oldValue, newValue) -> amountOfNotifications.getAndIncrement());

        // when
        measurementRecordChange.accept(record);

        // then
        assertThat(record.getMeasurementsTotalInSecsLongBinding().get()).isEqualTo(expectedTotal);
        assertThat(amountOfNotifications.get()).isEqualTo(expectedAmountOfNotifications);
    }

    private static Stream<Arguments> shouldUpdateMeasurementsTotalInSecsLongBindingWhenMeasurementsOrMeasurementInProgressIsChangedMethodSource() {
        return Stream.of(
                Arguments.of(
                        "should have measurementsTotalInSecsLongBinding equal to zero when no measurements",
                        (Consumer<StopwatchRecord>) record -> {
                        }, 0, 0
                ),
                Arguments.of(
                        "should update measurementsTotalInSecsLongBinding when measurement is changed",
                        (Consumer<StopwatchRecord>) record -> {
                            var measurement = createMeasurementWithDurationOf(100);
                            record.getMeasurementsProperty().add(measurement);
                            measurement.setStoppedAt(measurement.getStartedAt().plusSeconds(200));
                        }, 200, 2
                ),
                Arguments.of(
                        "should update measurementsTotalInSecsLongBinding when measurement is added",
                        (Consumer<StopwatchRecord>) record -> {
                            var measurement = createMeasurementWithDurationOf(100);
                            record.getMeasurementsProperty().add(measurement);
                        }, 100, 1
                ),
                Arguments.of(
                        "should be several updates and total has to be recalculated when measurement is added and then its durations is changed",
                        (Consumer<StopwatchRecord>) record -> {
                            var measurement = createMeasurementWithDurationOf(100);
                            record.getMeasurementsProperty().add(measurement);
                            measurement.setStoppedAt(measurement.getStartedAt().plusSeconds(200));
                        },
                        200, 2
                ),
                Arguments.of(
                        "should update measurementsTotalInSecsLongBinding when measurementInProgress is added",
                        (Consumer<StopwatchRecord>) record -> {
                            var measurement = createMeasurementWithDurationOf(100);
                            record.setMeasurementInProgress(measurement);
                        }, 100, 1
                ),
                Arguments.of(
                        "should update measurementsTotalInSecsLongBinding when measurementInProgress is changed",
                        (Consumer<StopwatchRecord>) record -> {
                            var measurement = createMeasurementWithDurationOf(100);
                            record.setMeasurementInProgress(measurement);
                            measurement.setStoppedAt(measurement.getStartedAt().plusSeconds(200));
                        }, 200, 2
                )
        );
    }

    private static StopwatchRecordMeasurement createMeasurementWithDurationOf(long durationInSeconds) {
        var measurement = new StopwatchRecordMeasurement();

        var startedAt = LocalTime.of(10, 0, 0);
        measurement.setStartedAt(startedAt);
        measurement.setStoppedAt(startedAt.plusSeconds(durationInSeconds));

        return measurement;
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("shouldCallChangedWhenObjectIsChangedMethodSource")
    void shouldCallChangedWhenObjectIsChanged(String description, Consumer<StopwatchRecord> measurementChange, int amountOfExpectedChanges) throws InterruptedException {
        // given
        var record = new StopwatchRecord();
        var amountOfNotifications = new AtomicInteger();
        record.getIsChangedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    amountOfNotifications.getAndIncrement();
                    System.out.println(amountOfNotifications.get());
                });

        // when
        measurementChange.accept(record);

        // then
        assertThat(amountOfNotifications.get()).isEqualTo(amountOfExpectedChanges);
    }

    private static Stream<Arguments> shouldCallChangedWhenObjectIsChangedMethodSource() {
        return Stream.of(
                Arguments.of(
                        "setTracked is called",
                        (Consumer<StopwatchRecord>) record -> record.setTracked(true),
                        1
                ),

                Arguments.of(
                        "setTracked is called multiple times",
                        (Consumer<StopwatchRecord>) record -> {
                            record.setTracked(true);
                            record.setTracked(false);
                            record.setTracked(true);
                        },
                        3
                ),

                Arguments.of(
                        "add measurement",
                        (Consumer<StopwatchRecord>) record -> record.getMeasurementsProperty().add(new StopwatchRecordMeasurement()),
                        2
                ),

                Arguments.of(
                        "total secs due to change of measurement  have changed",
                        (Consumer<StopwatchRecord>) record -> {
                            var measurement = new StopwatchRecordMeasurement();
                            var startedAt = LocalTime.of(10, 0, 0);
                            measurement.setStartedAt(startedAt);
                            measurement.setStoppedAt(startedAt.plusSeconds(100));
                            record.getMeasurementsProperty().add(measurement);

                            measurement.setStoppedAt(startedAt.plusSeconds(200));
                        },
                        2
                ),

                Arguments.of(
                        "setMeasurementInProgress is called",
                        (Consumer<StopwatchRecord>) record -> record.setMeasurementInProgress(new StopwatchRecordMeasurement()),
                        2
                )
        );
    }

    @Test
    void whenSetIsTrackedThenChangeTrackedStatus() {
        var record = new StopwatchRecord();
        assertThat(record.isTracked()).isFalse();

        record.setTracked(true);
        assertThat(record.isTracked()).isTrue();

        record.setTracked(false);
        assertThat(record.isTracked()).isFalse();
    }

}