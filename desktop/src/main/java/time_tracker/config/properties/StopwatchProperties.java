package time_tracker.config.properties;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StopwatchProperties {

    private StopwatchPanelProperties stopwatch;
    private StopwatchDatesProperties dates;
    private boolean devMode;
    private String folderWithData;
    private List<String> defaultRecords = new ArrayList<>();
    private StatisticsProperties statistics;
    private ReportProperties report;


    @Data
    public static class StopwatchPanelProperties {
        private DayStatisticProperties dayStatistic;
    }

    @Data
    public static class DayStatisticProperties {
        @JsonProperty("default")
        private DefaultDayStatisticProperties defaultData;
    }

    @Data
    public static class DefaultDayStatisticProperties {
        private String expectedWorkTime;
        private String comment;
    }

    @Data
    public static class StopwatchDatesProperties {
        private int amountOfDaysToShow;
        private String defaultFastEditButtonName;
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

    @Data
    public static class ReportProperties {
        private int numberOfDaysToShow;
        private boolean showTime;
        private GroupBy groupBy;
        private ExportFormat exportFormat;

        public enum GroupBy {
            REPORT, DATE;
        }

        public enum ExportFormat {
            JSON, CUSTOM
        }
    }


}