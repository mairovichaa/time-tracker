package time_tracker.component.stopwatch.measurement;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.config.GlobalContext;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecordMeasurement;

import java.util.concurrent.atomic.AtomicReference;

import static time_tracker.Utils.DATE_FORMAT_WITH_SHORT_DAY_NAME;
import static time_tracker.component.Utils.load;

@Log
public class ListOfMeasurementsForChosenRecordVBox extends VBox {

    @FXML
    private Label recordNameLabel;

    @FXML
    private Label recordDateLabel;
    @FXML
    private VBox finishedMeasurementsVBox;

    public ListOfMeasurementsForChosenRecordVBox() {
        load("/fxml/stopwatch/record/ListOfMeasurementsForChosenRecordVBox.fxml", this);

        var stopWatchAppState = GlobalContext.get(StopWatchAppState.class);

        var chosenStopwatchRecordProperty = stopWatchAppState.getChosenStopwatchRecord();

        ObservableList<Node> records = FXCollections.observableArrayList();

        AtomicReference<Runnable> removePreviousListener = new AtomicReference<>();

        chosenStopwatchRecordProperty.addListener(c -> {
            log.fine(() -> "chosen stopwatch record have been changed");

            if (removePreviousListener.get() != null) {
                removePreviousListener.get().run();
            }

            var stopwatchRecord = chosenStopwatchRecordProperty.get();
            var measurementsProperty = stopwatchRecord.getMeasurementsProperty();

            var invalidationListener = (InvalidationListener) ignored -> refreshRecords(records, measurementsProperty);

            removePreviousListener.set(() -> measurementsProperty.removeListener(invalidationListener));

            measurementsProperty.addListener(invalidationListener);
            refreshRecords(records, measurementsProperty);

            recordNameLabel.setText(stopwatchRecord.getName());
            recordDateLabel.setText(DATE_FORMAT_WITH_SHORT_DAY_NAME.format(stopwatchRecord.getDate()));
        });

        Bindings.bindContent(finishedMeasurementsVBox.getChildren(), records);
    }

    private static void refreshRecords(ObservableList<Node> records, ObservableList<StopwatchRecordMeasurement> measurementsProperty) {
        log.fine(() -> "refresh records");
        records.clear();
        measurementsProperty
                .stream()
                .map(MeasurementVBox::new)
                .forEach(records::add);
    }

}
