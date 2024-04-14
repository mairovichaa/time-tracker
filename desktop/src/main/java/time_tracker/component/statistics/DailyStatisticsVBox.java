package time_tracker.component.statistics;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import time_tracker.TimeTrackerApp;
import time_tracker.Utils;
import time_tracker.common.GlobalContext;
import time_tracker.component.statistics.model.DayStatistics;
import time_tracker.model.DayData;
import time_tracker.model.StopWatchAppState;

import java.time.LocalDate;
import java.util.function.Predicate;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static javafx.collections.FXCollections.observableArrayList;
import static time_tracker.Utils.DATE_FORMAT_WITH_SHORT_DAY_NAME;
import static time_tracker.component.Utils.load;

public class DailyStatisticsVBox extends VBox {

    @FXML
    private MFXComboBox<String> trackedComboBox;
    @FXML
    private MFXTableView<DayStatistics> table;
    private final StopWatchAppState stopWatchAppState;

    public DailyStatisticsVBox() {
        load("/fxml/statistics/DailyStatisticsVBox.fxml", this);
        stopWatchAppState = GlobalContext.get(StopWatchAppState.class);

        trackedComboBox.selectIndex(0);

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

        MFXTableColumn<DayStatistics> seeColumn = new MFXTableColumn<>("_");
        seeColumn.setRowCellFactory(stats -> {
            MFXTableRowCell<DayStatistics, String> dayStatisticsIntegerMFXTableRowCell = new MFXTableRowCell<>(ignored -> "");

            Button button = new Button("See");
            button.setOnMouseClicked(e -> {
                TimeTrackerApp.showStopwatch();
                LocalDate localDate = Utils.parseLocalDate(stats.getDate(), DATE_FORMAT_WITH_SHORT_DAY_NAME);
                stopWatchAppState.setChosenDate(localDate);
            });
            dayStatisticsIntegerMFXTableRowCell.leadingGraphicProperty().set(button);
            return dayStatisticsIntegerMFXTableRowCell;

        });

        table.getTableColumns().addAll(dateColumn, amountColumn, totalColumn, expectedColumn, timeToWorkLeftColumn, trackedColumn, seeColumn);

        table.autosizeColumnsOnInitialization();
        refresh();
    }

    @FXML
    private void refresh() {
        var dates = getDayStatistics();
        table.setItems(observableArrayList());
        table.setItems(dates);
    }

    private ObservableList<DayStatistics> getDayStatistics() {
        String trackedSelected = trackedComboBox.getSelectedItem();
        Predicate<DayData> dayDataPredicate;
        if (trackedSelected.equals("all")) {
            dayDataPredicate = it -> true;
        } else if (trackedSelected.equals("yes")) {
            dayDataPredicate = DayData::isTracked;
        } else {
            dayDataPredicate = Predicate.not(DayData::isTracked);
        }

        return stopWatchAppState.getDateToDayData()
                .values()
                .stream()
                .filter(dayDataPredicate)
                .sorted(comparing(DayData::getDate).reversed())
                .map(dayData -> {
                    var builder = DayStatistics.builder();
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
                    var date = DATE_FORMAT_WITH_SHORT_DAY_NAME.format(dayData.getDate());
                    return builder.date(date).build();
                }).collect(collectingAndThen(toList(), FXCollections::observableArrayList));
    }

}
