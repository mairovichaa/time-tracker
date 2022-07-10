package time_tracker.config.properties;

import lombok.Data;

@Data
public class StopwatchProperties {

    private StopwatchDatesProperties dates;
    private boolean devMode;
    private String folderWithData;

    @Data
    public static class StopwatchDatesProperties {
        private int amountOfDaysToShow;
    }

}