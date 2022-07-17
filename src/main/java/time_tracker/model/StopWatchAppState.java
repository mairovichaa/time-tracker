package time_tracker.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Data;
import time_tracker.annotation.NonNull;
import time_tracker.config.StopwatchSearchState;

import java.time.LocalDate;

@Data
public class StopWatchAppState {

    @NonNull
    private LocalDate chosenDate;

    @NonNull
    private ObjectProperty<LocalDate> chosenDateProperty = new SimpleObjectProperty<>();

    @NonNull
    private StopwatchSearchState searchState = new StopwatchSearchState();

    public void setChosenDate(@NonNull final LocalDate chosenDate) {
        this.chosenDate = chosenDate;
        this.chosenDateProperty.set(chosenDate);
    }
}
