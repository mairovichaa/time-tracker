package time_tracker.model;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;


public class StopwatchRecordMeasurementTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("shouldCallChangedWhenObjectIsChangedMethodSource")
    void shouldCallChangedWhenObjectIsChanged(String description, Consumer<StopwatchRecordMeasurement> measurementChange) {
        // given
        var record = new StopwatchRecordMeasurement();
        var isCalled = new AtomicBoolean(false);
        record.getIsChanged()
                .addListener(listener -> isCalled.set(true));

        // when
        measurementChange.accept(record);

        // then
        assertThat(isCalled.get()).isEqualTo(true);
    }

    private static Stream<Arguments> shouldCallChangedWhenObjectIsChangedMethodSource() {
        return Stream.of(
                Arguments.of(
                        "setId is called",
                        (Consumer<StopwatchRecordMeasurement>) record -> record.setId(10L)
                ),
                Arguments.of(
                        "setStartedAt is called",
                        (Consumer<StopwatchRecordMeasurement>) record -> record.setStartedAt(LocalTime.now())
                ),
                Arguments.of(
                        "startedAtProperty is called directly",
                        (Consumer<StopwatchRecordMeasurement>) record -> record.getStartedAtProperty().set(LocalTime.now())
                ),
                Arguments.of(
                        "setStoppedAt is called",
                        (Consumer<StopwatchRecordMeasurement>) record -> record.setStoppedAt(LocalTime.now())
                ),
                Arguments.of(
                        "stoppedAtProperty is called directly",
                        (Consumer<StopwatchRecordMeasurement>) record -> record.getStoppedAtProperty().set(LocalTime.now())
                ),
                Arguments.of(
                        "setNote is called",
                        (Consumer<StopwatchRecordMeasurement>) record -> record.setNote("some note")
                ),
                Arguments.of(
                        "noteProperty is called directly",
                        (Consumer<StopwatchRecordMeasurement>) record -> record.getNoteProperty().set("some note")
                ),
                Arguments.of(
                        "durationProperty is called directly",
                        (Consumer<StopwatchRecordMeasurement>) record -> record.getDurationProperty().set(10L)
                )
        );
    }
}
