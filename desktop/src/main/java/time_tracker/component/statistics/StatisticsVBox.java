package time_tracker.component.statistics;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.common.GlobalContext;
import time_tracker.config.properties.StopwatchProperties;

import java.util.logging.Level;

import static time_tracker.component.Utils.load;

@Log
public class StatisticsVBox extends VBox {

    @FXML
    private WeeklyStatisticsVBox weeklyStatisticsVBox;

    public StatisticsVBox() {
        load("/fxml/statistics/StatisticsVBox.fxml", this);
        log.log(Level.FINE, "Create StatisticsVBox");
        var stopwatchProperties = GlobalContext.get(StopwatchProperties.class);

        if (!stopwatchProperties.getStatistics().isShowWeekly()) {
            weeklyStatisticsVBox.setManaged(false);
            weeklyStatisticsVBox.setVisible(false);
        }
    }
}
