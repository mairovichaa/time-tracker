package time_tracker.component.statistics;

import io.github.palexdev.materialfx.beans.Alignment;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.common.GlobalContext;
import time_tracker.common.annotation.NonNull;
import time_tracker.component.common.Icon;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static time_tracker.component.Utils.load;
import static time_tracker.component.common.IconButton.initIconButton;

@Log
public class IntervalStatisticsVBox extends VBox {

    @FXML
    private MFXDatePicker startDatePicker;

    @FXML
    private MFXDatePicker endDatePicker;

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


    public IntervalStatisticsVBox() {
        load("/fxml/statistics/IntervalStatisticsVBox.fxml", this);

        startDatePicker.valueProperty()
                .addListener((observable, oldValue, newValue) -> recalculateStatistics());
        startDatePicker.popupAlignmentProperty()
                .set(Alignment.of(HPos.RIGHT, VPos.BOTTOM));

        endDatePicker.valueProperty()
                .addListener((observable, oldValue, newValue) -> recalculateStatistics());
        endDatePicker.popupAlignmentProperty()
                .set(Alignment.of(HPos.RIGHT, VPos.BOTTOM));

        var now = LocalDate.now();
        endDatePicker.valueProperty().set(now);
        startDatePicker.valueProperty().set(now.minusDays(7));
    }

    private void recalculateStatistics() {
        log.fine(() -> "calculate interval statistics");
        var startDate = startDatePicker.getValue();
        log.fine(() -> "startDate = " + startDate);
        if (startDate == null) {
            return;
        }

        var endDate = endDatePicker.getValue();
        log.fine(() -> "endDate = " + endDate);
        if (endDate == null) {
            return;
        }

        var stopWatchAppState = GlobalContext.get(StopWatchAppState.class);
        var dateFilter = createDateFilter(startDate, endDate);

        var records = stopWatchAppState.getDateToRecords()
                .keySet()
                .stream()
                .filter(dateFilter)
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
                .filter(dateFilter)
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

        initIconButton(overtimeIconLabel, 16, Icon.WORKSPACE_PREMIUM, List.of("icon-label-black"), List.of("label-icon-black"));
        initIconButton(totalAmountOfTimeIconLabel, 16, Icon.TASK_ALT, List.of("icon-label-black"), List.of("label-icon-black"));
        initIconButton(timeToWorkLeftIconLabel, 16, Icon.CONSTRUCTION, List.of("icon-label-black"), List.of("label-icon-black"));
        initIconButton(expectedIconLabel, 16, Icon.SCHEDULE, List.of("icon-label-black"), List.of("label-icon-black"));
    }

    private Predicate<LocalDate> createDateFilter(
            @NonNull final LocalDate startDate,
            @NonNull final LocalDate endDate
    ) {
        var startDateExclusively = startDate.minusDays(1);
        var endDateExclusively = endDate.plusDays(1);
        return date -> startDateExclusively.isBefore(date) && date.isBefore(endDateExclusively);
    }
}
