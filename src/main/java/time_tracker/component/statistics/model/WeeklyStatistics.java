package time_tracker.component.statistics.model;

import lombok.Builder;
import lombok.Getter;

@Builder
public class WeeklyStatistics {

    @Getter
    private String startDate;

    @Getter
    private String endDate;

    @Getter
    private String total;

}
