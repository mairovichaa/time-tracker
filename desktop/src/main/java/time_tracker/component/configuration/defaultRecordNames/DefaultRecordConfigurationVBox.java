package time_tracker.component.configuration.defaultRecordNames;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.component.common.DialogFactory;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.configuration.ConfigurationDefaultRecordModel;
import time_tracker.model.configuration.ConfigurationState;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;

@Log
public class DefaultRecordConfigurationVBox extends VBox {

    @FXML
    protected VBox defaultRecordNamesVBox;

    public DefaultRecordConfigurationVBox() {
        load("/fxml/configuration/defaultRecordNames/DefaultRecordConfigurationVBox.fxml", this);

        log.finest("Bind default record names to UI nodes");
        StopWatchAppState stopwatchAppState = CONTEXT.get(StopWatchAppState.class);
        ConfigurationState configurationState = stopwatchAppState.getConfigurationState();

        ObservableList<ConfigurationDefaultRecordModel> defaultRecords = configurationState.getConfigurationDefaultRecords();
        ObservableList<Node> defaultEntries = FXCollections.observableArrayList();
        defaultRecords.forEach(it -> defaultEntries.add(new DefaultRecordEntryVBox(it)));
        defaultRecords.addListener((ListChangeListener<ConfigurationDefaultRecordModel>) c -> {
            defaultEntries.clear();
            ObservableList<? extends ConfigurationDefaultRecordModel> current = c.getList();
            current.forEach(it -> defaultEntries.add(new DefaultRecordEntryVBox(it)));
        });

        Bindings.bindContent(defaultRecordNamesVBox.getChildren(), defaultEntries);
    }

    @FXML
    protected void add() {
        log.fine(() -> "'addDefaultRecord' button is clicked");
        DialogFactory.createAndShow(CreateDefaultRecordModal::new, "Add record");
    }
}
