package time_tracker.component.statistics;

import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import time_tracker.Utils;
import time_tracker.component.statistics.model.DayStatistics;
import time_tracker.common.GlobalContext;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopWatchAppState;
import time_tracker.service.TimeService;

import java.time.LocalDate;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static time_tracker.Utils.DATE_FORMAT_WITH_SHORT_DAY_NAME;
import static time_tracker.component.Utils.load;

public class DailyStatisticsVBox extends VBox {

    @FXML
    private MFXTableView<DayStatistics> table;

    private final TimeService timeService;
    private final StopwatchProperties stopwatchProperties;
    private final StopWatchAppState stopWatchAppState;

    public DailyStatisticsVBox() {
        load("/fxml/statistics/DailyStatisticsVBox.fxml", this);

        stopWatchAppState = GlobalContext.get(StopWatchAppState.class);
        timeService = GlobalContext.get(TimeService.class);
        stopwatchProperties = GlobalContext.get(StopwatchProperties.class);


        MFXTableColumn<DayStatistics> dateColumn = new MFXTableColumn<>("Date");
        dateColumn.setRowCellFactory(stats -> new MFXTableRowCell<>(DayStatistics::getDate));

        MFXTableColumn<DayStatistics> totalColumn = new MFXTableColumn<>("Total");
        totalColumn.setRowCellFactory(stats -> new MFXTableRowCell<>(DayStatistics::getTotal));

        MFXTableColumn<DayStatistics> timeToWorkLeftColumn = new MFXTableColumn<>("Time to work left");
        timeToWorkLeftColumn.setRowCellFactory(stats -> new MFXTableRowCell<>(DayStatistics::getTimeToWorkLeft));

        MFXTableColumn<DayStatistics> expectedColumn = new MFXTableColumn<>("Expected");
        expectedColumn.setRowCellFactory(stats -> new MFXTableRowCell<>(DayStatistics::getExpected));

        MFXTableColumn<DayStatistics> trackedColumn = new MFXTableColumn<>("Tracked");
        trackedColumn.setRowCellFactory(stats -> new MFXTableRowCell<>(DayStatistics::getTracked));

        MFXTableColumn<DayStatistics> amountColumn = new MFXTableColumn<>("Amount");
        amountColumn.setRowCellFactory(stats -> new MFXTableRowCell<>(DayStatistics::getAmount));

        table.getTableColumns().addAll(dateColumn, amountColumn, totalColumn, expectedColumn, timeToWorkLeftColumn, trackedColumn);

        table.autosizeColumnsOnInitialization();
        refresh();
    }

    @FXML
    private void refresh() {
        var stopwatchDatesProperties = stopwatchProperties.getDates();

        var today = timeService.today();
        var amountOfDaysToShow = stopwatchDatesProperties.getAmountOfDaysToShow();
        var dates = getDayStatistics(today, amountOfDaysToShow);
        table.setItems(FXCollections.observableArrayList());
        table.setItems(dates);
    }

    private ObservableList<DayStatistics> getDayStatistics(LocalDate today, int amountOfDaysToShow) {
        return IntStream.range(0, amountOfDaysToShow)
                .mapToObj(today::minusDays)
                .map(it -> {
                    var builder = DayStatistics.builder();
                    var date = DATE_FORMAT_WITH_SHORT_DAY_NAME.format(it);

                    var dayData = stopWatchAppState.getDateToDayData().get(it);
                    if (dayData != null) {
                        builder.amount(dayData.getAmountOfRecords());
                        var totalSecs = dayData.getTotalInSecs();
                        var duration = Utils.formatDuration(totalSecs);
                        builder.total(duration);

                        var timeToWorkLeftSecs = dayData.getTimeToWorkLeft();
                        var timeToWorkLeft = Utils.formatDuration(timeToWorkLeftSecs);
                        builder.timeToWorkLeft(timeToWorkLeft);

                        var trackedSecs = dayData.getTrackedInSecsProperty().get();
                        var tracked = Utils.formatDuration(trackedSecs);
                        builder.tracked(tracked);

                        var expectedSecs = dayData.getExpectedTotalInSecsProperty().get();
                        var expected = Utils.formatDuration(expectedSecs);
                        builder.expected(expected);
                    } else {
                        builder.total("-");
                    }

                    return builder.date(date).build();
                }).collect(collectingAndThen(toList(), FXCollections::observableArrayList));
    }

}
