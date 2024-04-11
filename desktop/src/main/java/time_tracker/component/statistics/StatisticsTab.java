package time_tracker.component.statistics;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import time_tracker.common.GlobalContext;
import time_tracker.config.properties.StopwatchProperties;

public class StatisticsTab extends Tab {

    public StatisticsTab() {
        super("Statistics");

        var intervalStatisticsVBox = new IntervalStatisticsVBox();
        var totalStatisticsVBox = new TotalStatisticsVBox();
        var firstColumn = new VBox();
        firstColumn.getChildren().addAll(totalStatisticsVBox, intervalStatisticsVBox);
        firstColumn.setSpacing(10);

        var stopwatchProperties = GlobalContext.get(StopwatchProperties.class);

        var secondColumn = new VBox();
        secondColumn.setSpacing(10);
        var dailyStatisticsVBox = new DailyStatisticsVBox();
        secondColumn.getChildren().add(dailyStatisticsVBox);

        var hBoxWrapper = new HBox();
        hBoxWrapper.setSpacing(10);
        hBoxWrapper.getChildren()
                .addAll(firstColumn, secondColumn);

        if (stopwatchProperties.getStatistics().isShowWeekly()) {
            var weeklyStatisticsVBox = new WeeklyStatisticsVBox();
            hBoxWrapper.getChildren().add(weeklyStatisticsVBox);
        }
        secondColumn.getChildren().add(new StatisticsChartsVBox());

        hBoxWrapper.setPadding(new Insets(10, 0, 0, 10));

        var scrollPane = new ScrollPane(hBoxWrapper);
        this.setContent(scrollPane);
    }
}
