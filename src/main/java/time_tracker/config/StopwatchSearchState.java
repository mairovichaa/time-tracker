package time_tracker.config;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import time_tracker.model.StopwatchRecord;

@Data
public class StopwatchSearchState {

    private StringProperty search = new SimpleStringProperty();
    private ObservableList<StopwatchRecord> found = FXCollections.observableArrayList();

}
