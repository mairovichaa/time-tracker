package time_tracker.component.statistics;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.component.common.Icon;
import time_tracker.component.common.IconUtils;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;
import static time_tracker.component.common.IconUtils.initIconLabeled;

@Log
public class TotalStatisticsVBox extends VBox {

    @FXML
    private Label startedAtLabel;

    @FXML
    private Label finishedAtLabel;

    @FXML
    private Label fromStartedAtToFinishedAtIconLabel;

    @FXML
    private Label totalAmountOfTimeLabel;
    @FXML
    private Label totalAmountOfTimeIconLabel;

    @FXML
    private Label expectedAmountOfTimeLabel;
    @FXML
    private Label expectedIconLabel;

    @FXML
    private Label overtimeLabel;
    @FXML
    private Label overtimeIconLabel;

    @FXML
    private Label timeToWorkLeftLabel;
    @FXML
    private Label timeToWorkLeftIconLabel;

    public TotalStatisticsVBox() {
        load("/fxml/statistics/TotalStatisticsVBox.fxml", this);
        recalculateStatistics();
    }

    private void recalculateStatistics() {
        log.fine(() -> "calculate total statistics");

        var stopWatchAppState = CONTEXT.get(StopWatchAppState.class);

        var records = stopWatchAppState.getDateToRecords()
                .keySet()
                .stream()
                .map(it -> stopWatchAppState.getDateToRecords().get(it))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        var totalInSecs = records.stream()
                .mapToLong(StopwatchRecord::getMeasurementsTotalInSecs)
                .sum();

        totalAmountOfTimeLabel.textProperty()
                .set(Utils.formatDuration(totalInSecs));

        var dayDataList = stopWatchAppState.getDateToDayData()
                .keySet()
                .stream()
                .map(it -> stopWatchAppState.getDateToDayData().get(it))
                .collect(Collectors.toList());

        var expectedTotal = dayDataList.stream()
                .mapToLong(it -> it.getExpectedTotalInSecsProperty().get())
                .sum();
        expectedAmountOfTimeLabel.textProperty()
                .set(Utils.formatDuration(expectedTotal));

        var overtime = Math.max(totalInSecs - expectedTotal, 0);
        overtimeLabel.textProperty()
                .set(Utils.formatDuration(overtime));

        var timeToWorkLeft = Math.max(expectedTotal - totalInSecs, 0);
        timeToWorkLeftLabel.textProperty()
                .set(Utils.formatDuration(timeToWorkLeft));

        IconUtils.initIconLabeled(overtimeIconLabel, 16, Icon.WORKSPACE_PREMIUM, List.of("icon-label-black"), List.of("label-icon-black"));
        IconUtils.initIconLabeled(totalAmountOfTimeIconLabel, 16, Icon.TASK_ALT, List.of("icon-label-black"), List.of("label-icon-black"));
        IconUtils.initIconLabeled(timeToWorkLeftIconLabel, 16, Icon.CONSTRUCTION, List.of("icon-label-black"), List.of("label-icon-black"));
        IconUtils.initIconLabeled(expectedIconLabel, 16, Icon.SCHEDULE, List.of("icon-label-black"), List.of("label-icon-black"));

        LocalDate startDate = stopWatchAppState.getDateToDayData()
                .keySet()
                .stream()
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());
        startedAtLabel.setText(Utils.formatLocalDate(startDate));

        LocalDate finishDate = stopWatchAppState.getDateToDayData()
                .keySet()
                .stream()
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());
        finishedAtLabel.setText(Utils.formatLocalDate(finishDate));
        IconUtils.initIconLabeled(fromStartedAtToFinishedAtIconLabel, 20, Icon.LINE_START, List.of("icon-label-black"), List.of("label-icon-black"));
    }
}
