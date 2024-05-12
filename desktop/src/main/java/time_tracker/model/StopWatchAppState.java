package time_tracker.model;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import lombok.Data;
import time_tracker.common.annotation.NonNull;
import time_tracker.model.configuration.ConfigurationState;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class StopWatchAppState {

    @NonNull
    private LocalDate chosenDate;

    @NonNull
    private ObjectProperty<StopwatchRecord> chosenStopwatchRecord = new SimpleObjectProperty<>(null);

    public void resetChosenStopwatchRecord() {
        chosenStopwatchRecord.setValue(null);
    }

    @NonNull
    private BooleanBinding hasChosenStopwatchRecord = new BooleanBinding() {
        {
            bind(chosenStopwatchRecord);
        }

        @Override
        protected boolean computeValue() {
            return chosenStopwatchRecord.getValue() != null;
        }
    };

    public boolean hasChosenRecord() {
        return hasChosenStopwatchRecord.get();
    }

    @NonNull
    private ObjectProperty<LocalDate> chosenDateProperty = new SimpleObjectProperty<>();

    @NonNull
    private StopwatchSearchState searchState = new StopwatchSearchState();

    @NonNull
    private ConfigurationState configurationState = new ConfigurationState();

    @NonNull
    private Map<LocalDate, ObservableList<StopwatchRecord>> dateToRecords = new HashMap<>();

    @NonNull
    private ObservableMap<LocalDate, DayData> dateToDayData = FXCollections.observableHashMap();

    @NonNull
    private ObservableList<StopwatchRecord> recordsForChosenDate = FXCollections.observableArrayList();

    @NonNull
    private ObjectProperty<WorkspaceItem> chosenWorkspaceItemObjectProperty = new SimpleObjectProperty<>();

    public void setChosenDate(@NonNull final LocalDate chosenDate) {
        this.chosenDate = chosenDate;
        this.chosenDateProperty.set(chosenDate);
    }

    public void setChosenWorkspace(@NonNull final WorkspaceItem workspaceItem) {
        chosenWorkspaceItemObjectProperty.setValue(workspaceItem);
    }
}
