package time_tracker.model.configuration;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import time_tracker.config.properties.StopwatchProperties;

@Getter
public class ConfigurationState {

    private final ObservableList<ConfigurationDefaultRecordModel> configurationDefaultRecords = FXCollections.observableArrayList();
    private final ObservableList<StopwatchProperties.FastEditButtonProperties> fastEditButtons = FXCollections.observableArrayList();
    private final ObjectProperty<ConfigurationItem> chosenItem = new SimpleObjectProperty<>();

}