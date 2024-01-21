package time_tracker.component.statistics;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class StatisticsTab extends Tab {

    public StatisticsTab() {
        super("Statistics");

        var intervalStatisticsVBox = new IntervalStatisticsVBox();
        var totalStatisticsVBox = new TotalStatisticsVBox();
        var firstColumn = new VBox();
        firstColumn.getChildren().addAll(totalStatisticsVBox, intervalStatisticsVBox);
        firstColumn.setSpacing(10);

        var weeklyStatisticsVBox = new WeeklyStatisticsVBox();
        var dailyStatisticsVBox = new DailyStatisticsVBox();

        var hBoxWrapper = new HBox();

        hBoxWrapper.setSpacing(10);

        hBoxWrapper.getChildren()
                .addAll(firstColumn, weeklyStatisticsVBox, dailyStatisticsVBox);
        hBoxWrapper.setPadding(new Insets(10, 0, 0, 10));

        var scrollPane = new ScrollPane(hBoxWrapper);
        this.setContent(scrollPane);
    }
}
