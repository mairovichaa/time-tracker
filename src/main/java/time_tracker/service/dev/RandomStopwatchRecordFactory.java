package time_tracker.service.dev;

import lombok.RequiredArgsConstructor;
import time_tracker.annotation.NonNull;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;
import time_tracker.service.StopwatchRecordService;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;

@RequiredArgsConstructor
public class RandomStopwatchRecordFactory {

    private final static int MAX_AMOUNT_OF_RECORDS = 10;
    private final static int MIN_AMOUNT_OF_RECORDS = 1;

    private final static int MAX_AMOUNT_OF_MEASUREMENTS = 7;
    private final static int MIN_AMOUNT_OF_MEASUREMENTS = 2;

    private final static int AMOUNT_OF_SECONDS_IN_DAY = 24 * 60 * 60;
    private final static int MAX_SECONDS_IN_WORK_INTERVAL = 40 * 60;
    private final static int MIN_SECONDS_IN_WORK_INTERVAL = 2 * 60;

    private final static int MAX_SECONDS_IN_REST_INTERVAL = 30 * 60;
    private final static int MIN_SECONDS_IN_REST_INTERVAL = 10 * 60;

    private final static int STARTING_DAY_AT_SECONDS = 9 * 60 * 60;

    @NonNull
    private final Random random = new Random();
    @NonNull
    private final StopwatchRecordService stopwatchRecordService;


    public void create() {
        var amountOfStopwatches = calculateNextValueUsingMinAndMax(MIN_AMOUNT_OF_RECORDS, MAX_AMOUNT_OF_RECORDS);
        for (int recordNumber = 0; recordNumber < amountOfStopwatches; recordNumber++) {
            var stopwatchName = "name #" + recordNumber;
            var stopwatchRecord = stopwatchRecordService.create(stopwatchName);
            createRandomMeasurements(stopwatchRecord);
        }
    }

    private void createRandomMeasurements(@NonNull final StopwatchRecord stopwatchRecord) {
        var amountOfMeasurementsUpperBound = random.nextInt(MAX_AMOUNT_OF_MEASUREMENTS);
        var amountOfMeasurements = Math.max(MIN_AMOUNT_OF_MEASUREMENTS, amountOfMeasurementsUpperBound);
        var measurements = stopwatchRecord.getMeasurementsProperty();
        var secondOfDay = STARTING_DAY_AT_SECONDS;

        var result = new ArrayList<StopwatchRecordMeasurement>();
        for (int measurementNumber = 0; measurementNumber < amountOfMeasurements; measurementNumber++) {
            var measurement = new StopwatchRecordMeasurement();
            var startedAt = LocalTime.ofSecondOfDay(secondOfDay);
            measurement.setStartedAt(startedAt);

            var amountOfSecsInWork = calculateNextValueUsingMinAndMax(MIN_SECONDS_IN_WORK_INTERVAL, MAX_SECONDS_IN_WORK_INTERVAL);
            secondOfDay += amountOfSecsInWork;

            if (secondOfDay >= AMOUNT_OF_SECONDS_IN_DAY) {
                return;
            }
            var stoppedAt = LocalTime.ofSecondOfDay(secondOfDay);
            measurement.setStoppedAt(stoppedAt);

            secondOfDay += calculateNextValueUsingMinAndMax(MIN_SECONDS_IN_REST_INTERVAL, MAX_SECONDS_IN_REST_INTERVAL);

            if (random.nextBoolean()) {
                var noteNumber = random.nextInt();
                var noteValue = "note #" + Math.abs(noteNumber);
                measurement.setNote(noteValue);
            }
            result.add(measurement);
//            measurements.add(measurement);
        }
        measurements.addAll(result);
    }

    private int calculateNextValueUsingMinAndMax(int min, int max) {
        var upperBound = random.nextInt(max);
        return Math.max(min, upperBound);
    }
}
