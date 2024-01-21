package time_tracker.config.properties;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StopwatchProperties {

    private StopwatchDatesProperties dates;
    private boolean devMode;
    private String folderWithData;
    private List<String> defaultRecords = new ArrayList<>();
    private StatisticsProperties statistics;

    @Data
    public static class StopwatchDatesProperties {
        private int amountOfDaysToShow;
        private List<FastEditButtonProperties> fastEditButtons = new ArrayList<>();
    }

    @Data
    public static class FastEditButtonProperties {
        // TODO add comment field
        private String name;
        private String expected;
    }

    @Data
    public static class StatisticsProperties {
        boolean showWeekly;
    }

}