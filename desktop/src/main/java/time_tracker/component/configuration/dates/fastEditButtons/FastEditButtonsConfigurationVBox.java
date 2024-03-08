package time_tracker.component.configuration.dates.fastEditButtons;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.common.GlobalContext;
import time_tracker.component.common.DialogFactory;
import time_tracker.config.properties.StopwatchProperties.FastEditButtonProperties;
import time_tracker.service.ConfigurationService;

import static time_tracker.component.Utils.load;

@Log
public class FastEditButtonsConfigurationVBox extends VBox {

    @FXML
    protected VBox fastEditButtonsVBox;

    private final ConfigurationService configurationService;

    public FastEditButtonsConfigurationVBox() {
        load("/fxml/configuration/dates/fastEditButtons/FastEditButtonsConfigurationVBox.fxml", this);

        configurationService = GlobalContext.get(ConfigurationService.class);

        log.finest("Bind default record names to UI nodes");
        ObservableList<FastEditButtonProperties> fastEditButtons = configurationService.getFastEditButtons();
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
        log.fine(() -> "'addFastEditButton' button is clicked");
        DialogFactory.createAndShow(
                stage -> new CreateFastEditButtonModal(stage, configurationService),
                "Rename record"
        );
    }

}
