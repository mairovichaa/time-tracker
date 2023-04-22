package time_tracker.component.stopwatch.measurement;

import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.utils.others.FunctionalStringConverter;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.config.GlobalContext;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;
import time_tracker.service.StopwatchRecordService;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static time_tracker.component.Utils.load;

@Log
public class MeasurementMoveVBox extends VBox {

    @FXML
    private Label recordNameLabel;
    @FXML
    private Button cancel;
    @FXML
    private MFXListView<StopwatchRecord> recordNamesList;

    private final Stage stage;

    public MeasurementMoveVBox(
            @NonNull final StopwatchRecordMeasurement measurement,
            @NonNull final Stage stage
    ) {
        load("/fxml/stopwatch/measurement/MeasurementMoveVBox.fxml", this);
        this.stage = stage;

        var stopWatchAppState = GlobalContext.get(StopWatchAppState.class);
        var chosenStopwatchRecord = stopWatchAppState.getChosenStopwatchRecord()
                .get();
        recordNameLabel.setText(chosenStopwatchRecord.getName());

        var chosenDate = stopWatchAppState.getChosenDate();
        var stopwatchRecords = stopWatchAppState.getDateToRecords()
                .get(chosenDate)
                .stream()
                .filter(it -> it != chosenStopwatchRecord)
                .collect(collectingAndThen(toList(), FXCollections::observableArrayList));

        StringConverter<StopwatchRecord> converter = FunctionalStringConverter.to(StopwatchRecord::getName);

        recordNamesList.setItems(stopwatchRecords);
        recordNamesList.setConverter(converter);
        recordNamesList.getSelectionModel().selectionProperty().addListener(
                (MapChangeListener<Integer, StopwatchRecord>) change -> {
                    if (change.wasAdded()) {
                        StopwatchRecord chosenRecord = change.getValueAdded();
                        var measurements = chosenRecord.getMeasurementsProperty();
                        chosenStopwatchRecord.getMeasurementsProperty().remove(measurement);
                        measurements.add(measurement);
                        var stopwatchRecordService = GlobalContext.get(StopwatchRecordService.class);
                        stopwatchRecordService.store();
                        stage.close();
                    }
                }
        );
    }

    @FXML
    protected void cancel() {
        log.fine("'Cancel' button is clicked");
        stage.close();
    }
}
