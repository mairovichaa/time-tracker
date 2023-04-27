package time_tracker.component.stopwatch.measurement;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
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

        ObservableList<MeasurementVBox> measurementsVBoxes = FXCollections.observableArrayList();
        showNoMeasurementsInfoWhenThereIsNoMeasurement(measurementsVBoxes);

        AtomicReference<Runnable> removePreviousListener = new AtomicReference<>();

        chosenStopwatchRecordProperty.addListener(c -> {
            log.fine(() -> "chosen stopwatch record have been changed");

            if (removePreviousListener.get() != null) {
                removePreviousListener.get().run();
            }

            var stopwatchRecord = chosenStopwatchRecordProperty.get();

            recordNameLabel.setText(stopwatchRecord.getName());
            recordDateLabel.setText(DATE_FORMAT_WITH_SHORT_DAY_NAME.format(stopwatchRecord.getDate()));

            var measurementsProperty = stopwatchRecord.getMeasurementsProperty();

            var invalidationListener = (InvalidationListener) ignored -> refreshMeasurements(measurementsVBoxes, measurementsProperty);
            removePreviousListener.set(() -> measurementsProperty.removeListener(invalidationListener));

            measurementsProperty.addListener(invalidationListener);
            refreshMeasurements(measurementsVBoxes, measurementsProperty);
        });

        Bindings.bindContent(finishedMeasurementsVBox.getChildren(), measurementsVBoxes);
    }

    private void showNoMeasurementsInfoWhenThereIsNoMeasurement(
            @NonNull final ObservableList<MeasurementVBox> measurementsVBoxes) {
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
            @NonNull final ObservableList<MeasurementVBox> measurementsVBoxes,
            @NonNull final ObservableList<StopwatchRecordMeasurement> measurementsProperty) {
        log.fine(() -> "refresh measurements");
        measurementsVBoxes.clear();
        measurementsProperty
                .stream()
                .map(MeasurementVBox::new)
                .forEach(measurementsVBoxes::add);
    }

}
