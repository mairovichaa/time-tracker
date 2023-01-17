package time_tracker.component.statistics;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.config.GlobalContext;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;

import java.util.Collection;
import java.util.stream.Collectors;

import static time_tracker.component.Utils.load;

@Log
public class TotalStatisticsVBox extends VBox {

    @FXML
    private Label totalLabel;

    @FXML
    private Label expectedLabel;

    @FXML
    private Label overtimeLabel;

    @FXML
    private Label timeToWorkLeftLabel;


    public TotalStatisticsVBox() {
        load("/fxml/statistics/TotalStatisticsVBox.fxml", this);
        recalculateStatistics();
    }

    private void recalculateStatistics() {
        log.fine(() -> "calculate total statistics");

        var stopWatchAppState = GlobalContext.get(StopWatchAppState.class);

        var records = stopWatchAppState.getDateToRecords()
                .keySet()
                .stream()
                .map(it -> stopWatchAppState.getDateToRecords().get(it))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        var totalInSecs = records.stream()
                .mapToLong(StopwatchRecord::getMeasurementsTotalInSecs)
                .sum();

        totalLabel.textProperty()
                .set(Utils.formatDuration(totalInSecs));

        var dayDataList = stopWatchAppState.getDateToDayData()
                .keySet()
                .stream()
                .map(it -> stopWatchAppState.getDateToDayData().get(it))
                .collect(Collectors.toList());

        var expectedTotal = dayDataList.stream()
                .mapToLong(it -> it.getExpectedTotalInSecsProperty().get())
                .sum();
        expectedLabel.textProperty()
                .set(Utils.formatDuration(expectedTotal));

        var overtime = Math.max(totalInSecs - expectedTotal, 0);
        overtimeLabel.textProperty()
                .set(Utils.formatDuration(overtime));

        var timeToWorkLeft = Math.max(expectedTotal - totalInSecs, 0);
        timeToWorkLeftLabel.textProperty()
                .set(Utils.formatDuration(timeToWorkLeft));
    }
}
