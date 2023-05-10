package time_tracker.component.statistics.model;

import lombok.Builder;
import lombok.Getter;

@Builder
public class DayStatistics {

    @Getter
    private String date;

    // TODO replace with property to have dynamically calculated value
    @Getter
    private String total;

    @Getter
    private String timeToWorkLeft;
    @Getter
    private String tracked;
    @Getter
    private String expected;
    @Getter
    private int amount;

}
