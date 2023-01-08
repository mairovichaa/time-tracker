package time_tracker.component.statistics;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;

public class StatisticsTab extends Tab {

    public StatisticsTab() {
        super("Statistics");
        var dailyStatisticsVBox = new DailyStatisticsVBox();
        var weeklyStatisticsVBox = new WeeklyStatisticsVBox();
        var intervalStatisticsVBox = new IntervalStatisticsVBox();

        var hBoxWrapper = new HBox();

        hBoxWrapper.setSpacing(10);

        hBoxWrapper.getChildren()
                .addAll(weeklyStatisticsVBox, dailyStatisticsVBox, intervalStatisticsVBox);
        hBoxWrapper.setPadding(new Insets(10, 0, 0, 10));
        this.setContent(hBoxWrapper);
    }
}
