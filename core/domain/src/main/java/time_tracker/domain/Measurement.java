package time_tracker.domain;

import lombok.Data;
import time_tracker.common.annotation.NonNull;

import java.time.LocalTime;

@Data
public class Measurement {
    @NonNull
    private Long id;
    @NonNull
    private LocalTime startedAt;
    @NonNull
    private LocalTime stoppedAt;
    @NonNull
    private String note;
}
