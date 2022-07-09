package time_tracker.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Data;
import time_tracker.annotation.NonNull;

import java.time.LocalDate;

@Data
public class StopWatchAppState {

    @NonNull
    private LocalDate chosenDate;

    @NonNull
    private ObjectProperty<LocalDate> chosenDateProperty = new SimpleObjectProperty<>();

    public void setChosenDate(@NonNull final LocalDate chosenDate) {
        this.chosenDate = chosenDate;
        this.chosenDateProperty.set(chosenDate);
    }
}
