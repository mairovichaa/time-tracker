package time_tracker.component.stopwatch.measurement;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.common.GlobalContext;
import time_tracker.common.annotation.NonNull;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecordMeasurement;

import java.util.concurrent.atomic.AtomicReference;

import static time_tracker.component.Utils.load;

@Log
public class ListOfMeasurementsForChosenRecordVBox extends VBox {

    @FXML
    private VBox finishedMeasurementsVBox;
    @FXML
    private VBox listOfMeasurementsWrapperVBox;
    @FXML
    private VBox noMeasurementsInfoVBox;

    public ListOfMeasurementsForChosenRecordVBox() {
        load("/fxml/stopwatch/record/ListOfMeasurementsForChosenRecordVBox.fxml", this);

        var stopWatchAppState = GlobalContext.get(StopWatchAppState.class);

        var chosenStopwatchRecordProperty = stopWatchAppState.getChosenStopwatchRecord();

        listOfMeasurementsWrapperVBox.visibleProperty()
                .bind(stopWatchAppState.getHasChosenStopwatchRecord());

        ObservableList<MeasurementPane> measurementsVBoxes = FXCollections.observableArrayList();
        showNoMeasurementsInfoWhenThereIsNoMeasurement(measurementsVBoxes);

        AtomicReference<Runnable> removePreviousListener = new AtomicReference<>();

        chosenStopwatchRecordProperty.addListener(c -> {
            log.fine(() -> "chosen stopwatch record have been changed");

            if (removePreviousListener.get() != null) {
                removePreviousListener.get().run();
            }

            if (!stopWatchAppState.hasChosenRecord()) {
                return;
            }

            var stopwatchRecord = chosenStopwatchRecordProperty.get();
            var measurementsProperty = stopwatchRecord.getMeasurementsProperty();

            var invalidationListener = (InvalidationListener) ignored -> refreshMeasurements(measurementsVBoxes, measurementsProperty);
            removePreviousListener.set(() -> measurementsProperty.removeListener(invalidationListener));

            measurementsProperty.addListener(invalidationListener);
            refreshMeasurements(measurementsVBoxes, measurementsProperty);
        });

        Bindings.bindContent(finishedMeasurementsVBox.getChildren(), measurementsVBoxes);
    }

    private void showNoMeasurementsInfoWhenThereIsNoMeasurement(
            @NonNull final ObservableList<MeasurementPane> measurementsVBoxes) {
        noMeasurementsInfoVBox.visibleProperty()
                .bind(new BooleanBinding() {
                    {
                        bind(measurementsVBoxes);
                    }

                    @Override
                    protected boolean computeValue() {
                        return measurementsVBoxes.isEmpty();
                    }
                });
        noMeasurementsInfoVBox.managedProperty().bind(
                noMeasurementsInfoVBox.visibleProperty()
        );
    }

    private void refreshMeasurements(
            @NonNull final ObservableList<MeasurementPane> measurementsVBoxes,
            @NonNull final ObservableList<StopwatchRecordMeasurement> measurementsProperty) {
        log.fine(() -> "refresh measurements");
        measurementsVBoxes.clear();
        measurementsProperty
                .stream()
                .map(MeasurementPane::new)
                .forEach(measurementsVBoxes::add);
    }

}
