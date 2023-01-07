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
import time_tracker.component.statistics.model.WeeklyStatistics;
import time_tracker.config.GlobalContext;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.DayData;
import time_tracker.model.StopWatchAppState;
import time_tracker.service.TimeService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static time_tracker.Utils.DATE_FORMAT;
import static time_tracker.component.Utils.load;

public class WeeklyStatisticsVBox extends VBox {
    @FXML
    private MFXTableView<WeeklyStatistics> table;
    private final StopWatchAppState stopWatchAppState;

    public WeeklyStatisticsVBox() {
        load("/fxml/statistics/WeeklyStatisticsVBox.fxml", this);

        stopWatchAppState = GlobalContext.get(StopWatchAppState.class);

        var timeService = GlobalContext.get(TimeService.class);
        var stopwatchProperties = GlobalContext.get(StopwatchProperties.class);
        var stopwatchDatesProperties = stopwatchProperties.getDates();

        var today = timeService.today();
        var amountOfDaysToShow = stopwatchDatesProperties.getAmountOfDaysToShow();

        MFXTableColumn<WeeklyStatistics> startAtColumn = new MFXTableColumn<>("Start at");
        startAtColumn.setRowCellFactory(stats -> new MFXTableRowCell<>(WeeklyStatistics::getStartDate));

        MFXTableColumn<WeeklyStatistics> endAtColumn = new MFXTableColumn<>("End at");
        endAtColumn.setRowCellFactory(stats -> new MFXTableRowCell<>(WeeklyStatistics::getEndDate));

        MFXTableColumn<WeeklyStatistics> totalColumn = new MFXTableColumn<>("Total");
        totalColumn.setRowCellFactory(stats -> new MFXTableRowCell<>(WeeklyStatistics::getTotal));

        table.getTableColumns().addAll(startAtColumn, endAtColumn, totalColumn);

        var stats = getWeeklyStatistics(today, amountOfDaysToShow);
        table.setItems(stats);
        table.autosizeColumnsOnInitialization();
    }

    private ObservableList<WeeklyStatistics> getWeeklyStatistics(LocalDate today, int amountOfDaysToShow) {
        return IntStream.range(0, amountOfDaysToShow)
                .mapToObj(today::minusDays)
                .map(it -> it.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
                .distinct()
                .map(it -> {
                    var weekStartsAt = DATE_FORMAT.format(it);
                    var weekEndsAt = DATE_FORMAT.format(it.plusDays(6));

                    var total = IntStream.range(0, 7)
                            .mapToObj(it::plusDays)
                            .map(stopWatchAppState.getDateToDayData()::get)
                            .filter(Objects::nonNull)
                            .mapToLong(DayData::getTotalInSecs)
                            .sum();

                    var duration = Utils.formatDuration(total);
                    return WeeklyStatistics.builder()
                            .startDate(weekStartsAt)
                            .endDate(weekEndsAt)
                            .total(duration)
                            .build();

                })
                .collect(collectingAndThen(toList(), FXCollections::observableArrayList));
    }
}
