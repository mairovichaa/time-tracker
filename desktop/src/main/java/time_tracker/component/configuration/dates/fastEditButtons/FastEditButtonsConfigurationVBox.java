package time_tracker.component.configuration.dates.fastEditButtons;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.component.common.DialogFactory;
import time_tracker.config.properties.StopwatchProperties.FastEditButtonProperties;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.configuration.ConfigurationState;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;

@Log
public class FastEditButtonsConfigurationVBox extends VBox {

    @FXML
    protected VBox fastEditButtonsVBox;

    public FastEditButtonsConfigurationVBox() {
        load("/fxml/configuration/dates/fastEditButtons/FastEditButtonsConfigurationVBox.fxml", this);

        StopWatchAppState stopwatchAppState = CONTEXT.get(StopWatchAppState.class);
        ConfigurationState configurationState = stopwatchAppState.getConfigurationState();

        log.finest("Bind default record names to UI nodes");
        ObservableList<FastEditButtonProperties> fastEditButtons = configurationState.getFastEditButtons();
        ObservableList<Node> defaultEntries = FXCollections.observableArrayList();

        fastEditButtons.forEach(it -> defaultEntries.add(new FastEditButtonEntryVBox(it)));

        fastEditButtons.addListener((ListChangeListener<FastEditButtonProperties>) c -> {
            defaultEntries.clear();
            ObservableList<? extends FastEditButtonProperties> current = c.getList();
            current.forEach(it -> defaultEntries.add(new FastEditButtonEntryVBox(it)));
        });

        Bindings.bindContent(fastEditButtonsVBox.getChildren(), defaultEntries);
    }

    @FXML
    protected void add() {
        log.fine("'addFastEditButton' button is clicked");
        DialogFactory.createAndShow(CreateFastEditButtonModal::new, "Rename record");
    }

}
