package time_tracker.component.stopwatch.record;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.common.GlobalContext;
import time_tracker.component.common.DialogFactory;
import time_tracker.component.common.Icon;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;

import static time_tracker.component.Utils.load;
import static time_tracker.component.common.IconButton.initIconButton;

@Log
public class ListOfRecordsForChosenDateVBox extends VBox {
    @FXML
    private VBox recordsVBox;

    @FXML
    private Button addRecordButton;


    public ListOfRecordsForChosenDateVBox() {
        load("/fxml/stopwatch/record/ListOfRecordsForChosenDateVBox.fxml", this);
        initIconButton(addRecordButton, 30, Icon.ADD_CIRCLE);

        var stopWatchAppState = GlobalContext.get(StopWatchAppState.class);

        ObservableList<Node> records = FXCollections.observableArrayList();
        stopWatchAppState.getRecordsForChosenDate()
                .addListener((ListChangeListener<StopwatchRecord>) c -> refresh(stopWatchAppState, records));
        Bindings.bindContent(recordsVBox.getChildren(), records);
        refresh(stopWatchAppState, records);

    }

    private static void refresh(StopWatchAppState stopWatchAppState, ObservableList<Node> records) {
        log.fine(() -> "stopwatch records have been changed");
        records.clear();
        stopWatchAppState.getRecordsForChosenDate()
                .stream()
                .map(StopwatchRecordVBox::new)
                .forEach(records::add);
    }

    @FXML
    private void addRecord() {
        log.fine("'Add record' button is clicked");
        DialogFactory.createAndShow(
                CreateRecordVBoxModal::new,
                "Add record"
        );
    }
}
