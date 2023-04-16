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

    @Data
    public static class StopwatchDatesProperties {
        private int amountOfDaysToShow;
        private List<FastEditButtonProperties> fastEditButtons = new ArrayList<>();
    }

    @Data
    public static class FastEditButtonProperties {
        private String name;
        private String expected;
    }

}