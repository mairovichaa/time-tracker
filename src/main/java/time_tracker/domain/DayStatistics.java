package time_tracker.domain;

import lombok.Data;
import time_tracker.annotation.NonNull;

import java.time.LocalDate;

@Data
public class DayStatistics {
    @NonNull
    private Long id;
    @NonNull
    private LocalDate date;
    @NonNull
    // TODO replace with duration?
    private Long expectedTotalInSecs;
    @NonNull
    private String note;
}
