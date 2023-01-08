package time_tracker.model;

import javafx.collections.FXCollections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DayDataTest {


    @ParameterizedTest(name = "{0}")
    @MethodSource("shouldUpdateTotalInSecsLongBindingWhenRecordsAreChangedMethodSource")
    void shouldUpdateTotalInSecsLongBindingWhenRecordsAreChanged(
            String description,
            Consumer<DayData> appDataChange,
            int expectedTotal,
            int expectedAmountOfNotifications
    ) {
        // given
        var startedAt = LocalDate.of(10, 1, 1);
        var records = FXCollections.<StopwatchRecord>observableArrayList();
        var dayData = new DayData(startedAt, records);

        var amountOfNotifications = new AtomicInteger();
        dayData.getTotalInSecsProperty()
                .addListener((observable, oldValue, newValue) -> amountOfNotifications.getAndIncrement());

        // when
        appDataChange.accept(dayData);

        // then
        assertThat(dayData.getTotalInSecs()).isEqualTo(expectedTotal);
        assertThat(amountOfNotifications.get()).isEqualTo(expectedAmountOfNotifications);
    }

    private static Stream<Arguments> shouldUpdateTotalInSecsLongBindingWhenRecordsAreChangedMethodSource() {
        return Stream.of(
                Arguments.of(
                        "should have measurementsTotalInSecsLongBinding equal to zero when no measurements",
                        (Consumer<DayData>) dayData -> {
                        }, 0, 0
                ),
                Arguments.of(
                        "should update measurementsTotalInSecsLongBinding when record is added",
                        (Consumer<DayData>) dayData -> {
                            var records = dayData.getRecords();
                            var stopwatchRecord = new StopwatchRecord();
                            var stopwatchRecordMeasurement = new StopwatchRecordMeasurement();
                            stopwatchRecordMeasurement.setStartedAt(LocalTime.of(10, 0, 0));
                            stopwatchRecordMeasurement.setStoppedAt(stopwatchRecordMeasurement.getStartedAt().plusSeconds(100));

                            stopwatchRecord.getMeasurementsProperty()
                                    .add(stopwatchRecordMeasurement);
                            records.add(stopwatchRecord);
                        }, 100, 1
                ),
                Arguments.of(
                        "should update measurementsTotalInSecsLongBinding when record is changed",
                        (Consumer<DayData>) dayData -> {
                            var records = dayData.getRecords();
                            var stopwatchRecord = new StopwatchRecord();
                            var measurement = new StopwatchRecordMeasurement();
                            measurement.setStartedAt(LocalTime.of(10, 0, 0));
                            measurement.setStoppedAt(measurement.getStartedAt().plusSeconds(100));

                            stopwatchRecord.getMeasurementsProperty()
                                    .add(measurement);
                            records.add(stopwatchRecord);

                            measurement.setStoppedAt(measurement.getStartedAt().plusSeconds(200));
                        }, 200, 2
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("shouldAmountOfRecordsPropertyWhenRecordsAreAddedOrRemovedMethodSource")
    void shouldAmountOfRecordsPropertyWhenRecordsAreAddedOrRemoved(
            String description,
            Consumer<DayData> appDataChange,
            int expectedAmountOfRecords,
            int expectedAmountOfNotifications
    ) {
        // given
        var startedAt = LocalDate.of(10, 1, 1);
        var records = FXCollections.<StopwatchRecord>observableArrayList();
        var dayData = new DayData(startedAt, records);

        var amountOfNotifications = new AtomicInteger();
        dayData.getAmountOfRecordsProperty()
                .addListener((observable, oldValue, newValue) -> amountOfNotifications.getAndIncrement());

        // when
        appDataChange.accept(dayData);

        // then
        assertThat(dayData.getAmountOfRecords()).isEqualTo(expectedAmountOfRecords);
        assertThat(amountOfNotifications.get()).isEqualTo(expectedAmountOfNotifications);
    }

    private static Stream<Arguments> shouldAmountOfRecordsPropertyWhenRecordsAreAddedOrRemovedMethodSource() {
        return Stream.of(
                Arguments.of(
                        "amountOfRecordsProperty should be equal to zero when no records",
                        (Consumer<DayData>) dayData -> {
                        }, 0, 0
                ),
                Arguments.of(
                        "should update amountOfRecordsProperty updated when record is added",
                        (Consumer<DayData>) dayData -> {
                            var records = dayData.getRecords();
                            var stopwatchRecord = new StopwatchRecord();
                            var stopwatchRecordMeasurement = new StopwatchRecordMeasurement();
                            stopwatchRecord.getMeasurementsProperty().add(stopwatchRecordMeasurement);
                            records.add(stopwatchRecord);
                        }, 1, 1
                ),
                Arguments.of(
                        "should update amountOfRecordsProperty when several records are added",
                        (Consumer<DayData>) dayData -> {
                            var records = dayData.getRecords();
                            var stopwatchRecord = new StopwatchRecord();
                            var measurement = new StopwatchRecordMeasurement();
                            stopwatchRecord.getMeasurementsProperty().add(measurement);
                            records.add(stopwatchRecord);
                            records.add(stopwatchRecord);
                        }, 2, 2
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("shouldChangeAmountOfTrackedTimeMethodSource")
    void shouldChangeAmountOfTrackedTime(
            String description,
            Consumer<DayData> appDataChange,
            int expectedTrackedInSecs,
            boolean expectedTracked,
            int expectedAmountOfNotifications
    ) {
        // given
        var startedAt = LocalDate.of(10, 1, 1);
        var records = FXCollections.<StopwatchRecord>observableArrayList();
        var dayData = new DayData(startedAt, records);

        var amountOfNotifications = new AtomicInteger();
        dayData.getTrackedInSecsProperty()
                .addListener((observable, oldValue, newValue) -> amountOfNotifications.getAndIncrement());

        // when
        appDataChange.accept(dayData);

        // then
        assertThat(dayData.getTrackedInSecsProperty().get()).isEqualTo(expectedTrackedInSecs);
        assertThat(dayData.getTracked().get()).isEqualTo(expectedTracked);
        assertThat(amountOfNotifications.get()).isEqualTo(expectedAmountOfNotifications);
    }

    private static Stream<Arguments> shouldChangeAmountOfTrackedTimeMethodSource() {
        return Stream.of(
                Arguments.of(
                        "amountOfRecordsProperty should be equal to zero when no records",
                        (Consumer<DayData>) dayData -> {
                        }, 0, true, 0
                ),
                Arguments.of(
                        "all time should be tracked if record is marked tracked",
                        (Consumer<DayData>) dayData -> {
                            var records = dayData.getRecords();
                            var stopwatchRecord = new StopwatchRecord();
                            var measurement = new StopwatchRecordMeasurement();
                            measurement.setStartedAt(LocalTime.of(10, 0, 0));
                            measurement.setStoppedAt(measurement.getStartedAt().plusSeconds(100));

                            stopwatchRecord.getMeasurementsProperty().add(measurement);

                            records.add(stopwatchRecord);

                            stopwatchRecord.setTracked(true);
                        }, 100, true, 1
                ),
                Arguments.of(
                        "no time should be tracked if record is marked tracked",
                        (Consumer<DayData>) dayData -> {
                            var records = dayData.getRecords();
                            var stopwatchRecord = new StopwatchRecord();
                            var measurement = new StopwatchRecordMeasurement();
                            measurement.setStartedAt(LocalTime.of(10, 0, 0));
                            measurement.setStoppedAt(measurement.getStartedAt().plusSeconds(100));

                            stopwatchRecord.getMeasurementsProperty().add(measurement);

                            records.add(stopwatchRecord);
                        }, 0, false, 0
                ),
                Arguments.of(
                        "all time should be tracked from all measurements if record is marked tracked",
                        (Consumer<DayData>) dayData -> {
                            var records = dayData.getRecords();
                            var stopwatchRecord = new StopwatchRecord();
                            var measurement = new StopwatchRecordMeasurement();
                            measurement.setStartedAt(LocalTime.of(10, 0, 0));
                            measurement.setStoppedAt(measurement.getStartedAt().plusSeconds(100));

                            stopwatchRecord.getMeasurementsProperty().add(measurement);

                            var measurement2 = new StopwatchRecordMeasurement();
                            measurement2.setStartedAt(LocalTime.of(10, 0, 0));
                            measurement2.setStoppedAt(measurement2.getStartedAt().plusSeconds(100));
                            stopwatchRecord.getMeasurementsProperty().add(measurement2);

                            records.add(stopwatchRecord);

                            stopwatchRecord.setTracked(true);
                        }, 200, true, 1
                ),
                Arguments.of(
                        "tracked in secs should be notified several times if tracked status is changed several times",
                        (Consumer<DayData>) dayData -> {
                            var records = dayData.getRecords();
                            var stopwatchRecord = new StopwatchRecord();
                            var measurement = new StopwatchRecordMeasurement();
                            measurement.setStartedAt(LocalTime.of(10, 0, 0));
                            measurement.setStoppedAt(measurement.getStartedAt().plusSeconds(100));

                            stopwatchRecord.getMeasurementsProperty().add(measurement);

                            var measurement2 = new StopwatchRecordMeasurement();
                            measurement2.setStartedAt(LocalTime.of(10, 0, 0));
                            measurement2.setStoppedAt(measurement2.getStartedAt().plusSeconds(100));
                            stopwatchRecord.getMeasurementsProperty().add(measurement2);

                            records.add(stopwatchRecord);

                            stopwatchRecord.setTracked(true);
                            stopwatchRecord.setTracked(false);
                            stopwatchRecord.setTracked(true);
                        }, 300, true, 3
                )
        );
    }

}