package time_tracker.model;

import lombok.Data;

import java.time.LocalTime;

@Data
public class StopwatchRecordMeasurement {
    private LocalTime startedAt;
    private LocalTime stoppedAt;
}
