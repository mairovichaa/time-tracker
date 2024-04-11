package time_tracker.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;

@Data
public class StopwatchSearchState {

    private BooleanProperty tracked = new SimpleBooleanProperty();
    private StringProperty search = new SimpleStringProperty();
    private ObservableList<String> found = FXCollections.observableArrayList();
    private StringProperty chosenRecordName = new SimpleStringProperty();
    private ObservableList<StopwatchRecord> recordsForChosenName = FXCollections.observableArrayList();

}
