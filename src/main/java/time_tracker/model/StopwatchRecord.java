package time_tracker.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
public class StopwatchRecord {

    @NonNull
    private String name;

    @NonNull
    private List<StopwatchRecordMeasurement> measurements = new ArrayList<>();

    @NonNull
    private ObservableList<StopwatchRecordMeasurement> measurementsProperty = FXCollections.observableArrayList(measurements);

    private StopwatchRecordMeasurement measurementInProgress;

    private BooleanProperty hasMeasurementInProgress = new SimpleBooleanProperty(false);

}
