package time_tracker.component.stopwatch;

import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import time_tracker.config.GlobalContext;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopWatchAppState;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static time_tracker.component.Utils.load;

public class ListOfDatesVbox extends VBox {

    @FXML
    private MFXTableView<LocalDate> table;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd (EEE)");

    public ListOfDatesVbox() {
        load("/fxml/stopwatch/ListOfDatesVbox.fxml", this);

        var stopWatchAppState = GlobalContext.get(StopWatchAppState.class);
        var stopwatchProperties = GlobalContext.get(StopwatchProperties.class);
        var stopwatchDatesProperties = stopwatchProperties.getDates();

        var today = stopWatchAppState.getChosenDate();
        var amountOfDaysToShow = stopwatchDatesProperties.getAmountOfDaysToShow();

        MFXTableColumn<LocalDate> dateColumn = new MFXTableColumn<>("Date", true, LocalDate::compareTo);

        dateColumn.setRowCellFactory(person -> new MFXTableRowCell<>(DATE_FORMAT::format));
        table.getTableColumns().addAll(dateColumn);

        var dates = IntStream.range(0, amountOfDaysToShow)
                .mapToObj(today::minusDays)
                .collect(collectingAndThen(toList(), FXCollections::observableArrayList));
        table.setItems(dates);
        table.autosizeColumnsOnInitialization();

        table.getSelectionModel().selectionProperty()
                .addListener((MapChangeListener<Integer, LocalDate>) change -> {
                    if (change.wasAdded()) {
                        LocalDate chosenDate = change.getValueAdded();
                        stopWatchAppState.setChosenDate(chosenDate);
                    }
                });
        table.getSelectionModel().selectIndex(0);
    }
}
