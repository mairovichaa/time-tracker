package time_tracker.component;

import javafx.scene.control.TabPane;
import time_tracker.component.interval.IntervalTab;
import time_tracker.component.search.SearchTab;
import time_tracker.component.statistics.StatisticsTab;
import time_tracker.component.stopwatch.StopWatchTab;

public class TimeTrackerTabPane extends TabPane {

    public TimeTrackerTabPane() {
        var stopWatchTab = new StopWatchTab();
        var searchTab = new SearchTab();
        var statisticsTab = new StatisticsTab();
        var intervalTab = new IntervalTab();
        this.getTabs().addAll(stopWatchTab, searchTab, statisticsTab, intervalTab);
    }
}
