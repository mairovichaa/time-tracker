package time_tracker.component;

import javafx.scene.control.TabPane;
import time_tracker.component.configuration.ConfigurationTab;
import time_tracker.component.interval.IntervalTab;
import time_tracker.component.report.ReportTab;
import time_tracker.component.search.SearchTab;
import time_tracker.component.statistics.StatisticsTab;
import time_tracker.component.stopwatch.StopWatchTab;

public class TimeTrackerTabPane extends TabPane {

    public static TimeTrackerTabPane INSTANCE;

    private final StopWatchTab stopWatchTab;

    public TimeTrackerTabPane() {
        stopWatchTab = new StopWatchTab();
        var searchTab = new SearchTab();
        var statisticsTab = new StatisticsTab();
        var reportTab = new ReportTab();
        var intervalTab = new IntervalTab();
        var configurationTab = new ConfigurationTab();
        this.getTabs().addAll(stopWatchTab, searchTab, statisticsTab, reportTab, intervalTab, configurationTab);

        // todo fix
        INSTANCE = this;
    }

    public void openStopWatchTab() {
        this.getSelectionModel().select(stopWatchTab);
    }
}
