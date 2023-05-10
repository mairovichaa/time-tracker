package time_tracker.component.statistics;

import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.beans.property.LongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import time_tracker.Utils;
import time_tracker.component.statistics.model.DayStatistics;
import time_tracker.component.statistics.model.WeeklyStatistics;
import time_tracker.common.GlobalContext;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.DayData;
import time_tracker.model.StopWatchAppState;
import time_tracker.service.TimeService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static time_tracker.Utils.DATE_FORMAT;
import static time_tracker.component.Utils.load;

public class WeeklyStatisticsVBox extends VBox {
    @FXML
    private MFXTableView<WeeklyStatistics> table;
    private final StopWatchAppState stopWatchAppState;

    private final TimeService timeService;
    private final int amountOfDaysToShow = 500;

    public WeeklyStatisticsVBox() {
        load("/fxml/statistics/WeeklyStatisticsVBox.fxml", this);

        stopWatchAppState = GlobalContext.get(StopWatchAppState.class);
        timeService = GlobalContext.get(TimeService.class);

        MFXTableColumn<WeeklyStatistics> startAtColumn = new MFXTableColumn<>("Start at");
        startAtColumn.setRowCellFactory(stats -> new MFXTableRowCell<>(WeeklyStatistics::getStartDate));

        MFXTableColumn<WeeklyStatistics> endAtColumn = new MFXTableColumn<>("End at");
        endAtColumn.setRowCellFactory(stats -> new MFXTableRowCell<>(WeeklyStatistics::getEndDate));

        MFXTableColumn<WeeklyStatistics> totalColumn = new MFXTableColumn<>("Total");
        totalColumn.setRowCellFactory(stats -> new MFXTableRowCell<>(WeeklyStatistics::getTotal));

        MFXTableColumn<WeeklyStatistics> timeToWorkLeftColumn = new MFXTableColumn<>("Time to work left");
        timeToWorkLeftColumn.setRowCellFactory(stats -> new MFXTableRowCell<>(WeeklyStatistics::getTimeToWorkLeft));

        MFXTableColumn<WeeklyStatistics> expectedColumn = new MFXTableColumn<>("Expected");
        expectedColumn.setRowCellFactory(stats -> new MFXTableRowCell<>(WeeklyStatistics::getExpected));

        MFXTableColumn<WeeklyStatistics> trackedColumn = new MFXTableColumn<>("Tracked");
        trackedColumn.setRowCellFactory(stats -> new MFXTableRowCell<>(WeeklyStatistics::getTracked));

        table.getTableColumns().addAll(startAtColumn, endAtColumn, totalColumn, expectedColumn, timeToWorkLeftColumn, trackedColumn);

        refresh();
        table.autosizeColumnsOnInitialization();
    }

    @FXML
    private void refresh() {
        var today = timeService.today();
        var stats = getWeeklyStatistics(today);
        table.setItems(stats);
    }

    private ObservableList<WeeklyStatistics> getWeeklyStatistics(LocalDate today) {
        return IntStream.range(0, amountOfDaysToShow)
                .mapToObj(today::minusDays)
                .map(it -> it.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
                .distinct()
                .map(it -> {
                    var weekStartsAt = DATE_FORMAT.format(it);
                    var weekEndsAt = DATE_FORMAT.format(it.plusDays(6));

                    var dayDataList = IntStream.range(0, 7)
                            .mapToObj(it::plusDays)
                            .map(stopWatchAppState.getDateToDayData()::get)
                            .filter(Objects::nonNull)
                            .collect(toList());

                    var totalInSecs = dayDataList.stream()
                            .mapToLong(DayData::getTotalInSecs)
                            .sum();
                    var total = Utils.formatDuration(totalInSecs);

                    var timeToWorkLeftSecs = dayDataList.stream()
                            .mapToLong(DayData::getTimeToWorkLeft)
                            .sum();
                    var timeToWorkLeft = Utils.formatDuration(timeToWorkLeftSecs);

                    var trackedInSecs = dayDataList.stream()
                            .mapToLong(dd -> dd.getTrackedInSecsProperty().get())
                            .sum();
                    var tracked = Utils.formatDuration(trackedInSecs);

                    var expectedInSecs = dayDataList.stream()
                            .mapToLong(dd -> dd.getExpectedTotalInSecsProperty().get())
                            .sum();
                    var expected = Utils.formatDuration(expectedInSecs);

                    return WeeklyStatistics.builder()
                            .startDate(weekStartsAt)
                            .endDate(weekEndsAt)
                            .total(total)
                            .timeToWorkLeft(timeToWorkLeft)
                            .tracked(tracked)
                            .expected(expected)
                            .build();

                })
                .collect(collectingAndThen(toList(), FXCollections::observableArrayList));
    }
}
