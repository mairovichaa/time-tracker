package time_tracker.component.stopwatch;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.config.GlobalContext;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;
import time_tracker.service.StopwatchRecordService;

import static time_tracker.component.Utils.load;

@Log
public class ListOfRecordsForChosenDateVBox extends VBox {
    @FXML
    private VBox recordsVBox;

    @FXML
    private MFXScrollPane scrollPane;
    @NonNull
    private final StopwatchRecordService stopwatchRecordService;

    public ListOfRecordsForChosenDateVBox() {
        load("/fxml/stopwatch/ListOfRecordsForChosenDateVBox.fxml", this);

        this.stopwatchRecordService = GlobalContext.get(StopwatchRecordService.class);
        var stopWatchAppState = GlobalContext.get(StopWatchAppState.class);

        ObservableList<Node> records = FXCollections.observableArrayList();
        stopWatchAppState.getRecordsForChosenDate()
                .addListener((ListChangeListener<StopwatchRecord>) c -> {
                    log.fine(() -> "stopwatch records have been changed");
                    records.clear();
                    stopWatchAppState.getRecordsForChosenDate()
                            .stream()
                            .map(StopwatchRecordVBox::new)
                            .forEach(records::add);
                });
        Bindings.bindContent(recordsVBox.getChildren(), records);
    }

    @FXML
    protected void save() {
        stopwatchRecordService.store();
    }
}
