package time_tracker.component.stopwatch;

import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import time_tracker.TimeTrackerApp;
import time_tracker.component.stopwatch.date.CreateDateVBox;
import time_tracker.common.GlobalContext;
import time_tracker.model.DayData;
import time_tracker.model.StopWatchAppState;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.function.Function;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static time_tracker.Utils.DATE_FORMAT_WITH_SHORT_DAY_NAME;
import static time_tracker.component.Utils.load;

@Log
public class ListOfDatesVbox extends VBox {

    @FXML
    private MFXTableView<LocalDate> table;

    public ListOfDatesVbox() {
        load("/fxml/stopwatch/ListOfDatesVbox.fxml", this);

        var stopWatchAppState = GlobalContext.get(StopWatchAppState.class);

        MFXTableColumn<LocalDate> dateColumn = new MFXTableColumn<>("Date", true, LocalDate::compareTo);
        dateColumn.setRowCellFactory(date -> new MFXTableRowCell<>(DATE_FORMAT_WITH_SHORT_DAY_NAME::format));

        MFXTableColumn<LocalDate> trackedColumn = new MFXTableColumn<>("T", true);
        trackedColumn.setRowCellFactory(date -> new MFXTableRowCell<>(it -> {
            var dayData = stopWatchAppState.getDateToDayData()
                    .get(it);
            if (dayData == null){
                return "+";
            }
            var isTracked = dayData.isTracked();
            return isTracked ? "+" : "-";
        }));

        table.getTableColumns().addAll(dateColumn, trackedColumn);
        stopWatchAppState.getDateToDayData()
                .addListener((MapChangeListener<LocalDate, DayData>) change -> refresh());

        refresh();
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

    @FXML
    protected void add() {
        log.fine("'add' button is clicked");
        var dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(TimeTrackerApp.primaryStage);
        var dialogVbox = new CreateDateVBox(dialog);
        var dialogScene = new Scene(dialogVbox);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    @FXML
    protected void refresh(){
        var stopWatchAppState = GlobalContext.get(StopWatchAppState.class);
        var dates = stopWatchAppState.getDateToDayData()
                .keySet()
                .stream()
                .sorted(Comparator.<LocalDate, LocalDate>comparing(Function.identity()).reversed())
                .collect(collectingAndThen(toList(), FXCollections::observableArrayList));
        table.getItems().clear();
        table.setItems(dates);
    }
}
