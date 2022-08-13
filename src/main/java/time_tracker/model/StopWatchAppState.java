package time_tracker.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import time_tracker.annotation.NonNull;
import time_tracker.config.StopwatchSearchState;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class StopWatchAppState {

    @NonNull
    private LocalDate chosenDate;

    @NonNull
    private ObjectProperty<LocalDate> chosenDateProperty = new SimpleObjectProperty<>();

    @NonNull
    private StopwatchSearchState searchState = new StopwatchSearchState();

    @NonNull
    private Map<LocalDate, ObservableList<StopwatchRecord>> dateToRecords = new HashMap<>();

    @NonNull
    private Map<LocalDate, DayData> dateToDayData = new HashMap<>();


    @NonNull
    private ObservableList<StopwatchRecord> recordsForChosenDate = FXCollections.observableArrayList();

    public void setChosenDate(@NonNull final LocalDate chosenDate) {
        this.chosenDate = chosenDate;
        this.chosenDateProperty.set(chosenDate);
    }
}
