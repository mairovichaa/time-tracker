package time_tracker.component.configuration;

import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.cell.MFXListCell;
import io.github.palexdev.materialfx.utils.others.FunctionalStringConverter;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.configuration.ConfigurationItem;

import java.util.Arrays;

import static java.util.stream.Collectors.toCollection;
import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;

public class ConfigurationMenuVBox extends VBox {

    @FXML
    protected MFXListView<ConfigurationItem> configurationListView;

    public ConfigurationMenuVBox() {
        load("/fxml/configuration/ConfigurationMenuVBox.fxml", this);
        ObservableList<ConfigurationItem> configurationItems = Arrays.stream(ConfigurationItem.values())
                .collect(toCollection(FXCollections::observableArrayList));

        configurationListView.setItems(configurationItems);
        StopWatchAppState stopwatchAppState = CONTEXT.get(StopWatchAppState.class);

        configurationListView.getSelectionModel().selectionProperty()
                .addListener((MapChangeListener<Integer, ConfigurationItem>) change -> {
                            if (change.wasAdded()) {
                                ConfigurationItem valueAdded = change.getValueAdded();
                                stopwatchAppState.getConfigurationState()
                                        .getChosenItem()
                                        .setValue(valueAdded);
                            }
                        }
                );
        stopwatchAppState.getConfigurationState()
                .getChosenItem()
                .addListener((observable, oldValue, newValue) -> {
                    int indexOfConfigurationState = configurationItems.indexOf(newValue);
                    configurationListView.getSelectionModel().selectIndex(indexOfConfigurationState);
                });
        configurationListView.setConverter(FunctionalStringConverter.to(ConfigurationItem::getName));
        configurationListView.setCellFactory(item ->
        {
            if (item.equals(configurationItems.get(0))) {
                return new MFXListCell<>(configurationListView, item) {
                    @Override
                    protected void initialize() {
                        super.initialize();
                        getStyleClass().add("mfx-list-cell-first");
                    }
                };
            }

            int lastIndex = configurationItems.size() - 1;
            if (item.equals(configurationItems.get(lastIndex))) {
                return new MFXListCell<>(configurationListView, item) {
                    @Override
                    protected void initialize() {
                        super.initialize();
                        getStyleClass().add("mfx-list-cell-last");
                    }
                };
            }

            return new MFXListCell<>(configurationListView, item);
        });
    }
}
