package time_tracker.domain;

import lombok.Data;
import time_tracker.annotation.NonNull;
import time_tracker.model.StopwatchRecordMeasurement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class Record {
    @NonNull
    private Long id;
    @NonNull
    private String name;

    @NonNull
    private LocalDate date;

    private boolean tracked;

    @NonNull
    private List<Measurement> measurements = new ArrayList<>();
}
