package time_tracker.component.stopwatch;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.config.GlobalContext;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopwatchRecord;
import time_tracker.service.StopwatchRecordService;

import java.io.IOException;

import static time_tracker.component.Utils.load;

@Log
public class ListOfRecordsForChosenDateVBox extends VBox {
    @FXML
    private VBox recordsVBox;

    @FXML
    private MFXScrollPane scrollPane;
    @NonNull
    private final StopwatchRecordService stopwatchRecordService;
    @NonNull
    private final StopwatchProperties stopwatchProperties;

    public ListOfRecordsForChosenDateVBox() {
        //        TODO it has too small space - it's not possible to see comment
        load("/fxml/stopwatch/ListOfRecordsForChosenDateVBox.fxml", this);

        this.stopwatchRecordService = GlobalContext.get(StopwatchRecordService.class);
        this.stopwatchProperties = GlobalContext.get(StopwatchProperties.class);


        // TODO fix bug that they not initialized on start
        ObservableList<Node> records = FXCollections.observableArrayList();
        var stopwatchRecords = stopwatchRecordService.findAll();
        stopwatchRecords.addListener((ListChangeListener<StopwatchRecord>) c -> {
            log.fine(() -> "stopwatch records have been changed");
            records.clear();
            stopwatchRecords.stream()
                    .map(StopwatchRecordVBox::new)
                    .forEach(records::add);
        });
        Bindings.bindContent(recordsVBox.getChildren(), records);

        this.stopwatchRecordService.refreshRecords();
    }

    @FXML
    protected void save() {
        stopwatchRecordService.store();
    }
}
