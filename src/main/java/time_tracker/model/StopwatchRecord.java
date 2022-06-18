package time_tracker.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class StopwatchRecord {

    @NonNull
    private String name;

    @NonNull
    private List<StopwatchRecordMeasurement> measurements;
}
