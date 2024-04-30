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
import time_tracker.service.ConfigurationService;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;

@Log
public class DefaultRecordConfigurationVBox extends VBox {

    @FXML
    protected VBox defaultRecordNamesVBox;

    private final ConfigurationService configurationService;


    public DefaultRecordConfigurationVBox() {
        load("/fxml/configuration/defaultRecordNames/DefaultRecordConfigurationVBox.fxml", this);

        configurationService = CONTEXT.get(ConfigurationService.class);

        log.finest("Bind default record names to UI nodes");
        ObservableList<String> defaultRecordNames = configurationService.getDefaultRecordNames();
        ObservableList<Node> defaultEntries = FXCollections.observableArrayList();

        defaultRecordNames.forEach(it -> defaultEntries.add(new DefaultRecordEntryVBox(it)));

        defaultRecordNames.addListener((ListChangeListener<String>) c -> {
            defaultEntries.clear();
            ObservableList<? extends String> current = c.getList();
            current.forEach(it -> defaultEntries.add(new DefaultRecordEntryVBox(it)));
        });

        Bindings.bindContent(defaultRecordNamesVBox.getChildren(), defaultEntries);
    }

    @FXML
    protected void add() {
        log.fine(() -> "'addDefaultRecord' button is clicked");
        DialogFactory.createAndShow(
                stage -> new CreateDefaultRecordModal(stage, configurationService),
                "Add record"
        );
    }
}
