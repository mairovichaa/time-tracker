package time_tracker.component.statistics;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;

public class StatisticsTab extends Tab {

    public StatisticsTab() {
        super("Statistics");
        var dailyStatisticsVBox = new DailyStatisticsVBox();
        var weeklyStatisticsVBox = new WeeklyStatisticsVBox();
        var intervalStatisticsVBox = new IntervalStatisticsVBox();
        var totalStatisticsVBox = new TotalStatisticsVBox();

        var hBoxWrapper = new HBox();

        hBoxWrapper.setSpacing(10);

        hBoxWrapper.getChildren()
                .addAll(totalStatisticsVBox, intervalStatisticsVBox, weeklyStatisticsVBox, dailyStatisticsVBox);
        hBoxWrapper.setPadding(new Insets(10, 0, 0, 10));

        var scrollPane = new ScrollPane(hBoxWrapper);
        this.setContent(scrollPane);
    }
}