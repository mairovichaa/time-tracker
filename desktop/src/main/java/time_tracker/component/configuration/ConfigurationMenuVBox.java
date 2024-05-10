package time_tracker.component.configuration;

import io.github.palexdev.materialfx.controls.MFXListView;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import time_tracker.common.annotation.NonNull;
import time_tracker.component.configuration.dates.fastEditButtons.FastEditButtonsConfigurationVBox;
import time_tracker.component.configuration.defaultRecordNames.DefaultRecordConfigurationVBox;
import time_tracker.component.configuration.stopwatch.DayStatisticDefaultVBox;

import static time_tracker.component.Utils.load;

public class ConfigurationMenuVBox extends VBox {

    @FXML
    protected MFXListView<String> configurationListView;

    public ConfigurationMenuVBox() {
        load("/fxml/configuration/ConfigurationMenuVBox.fxml", this);
    }

    public void init(@NonNull final VBox configurationVBox) {
        var defaultRecordNamesWrapperVBox = new DefaultRecordConfigurationVBox();
        var fastEditButtonsConfigurationVBox = new FastEditButtonsConfigurationVBox();
        var dayStatisticDefaultVBox = new DayStatisticDefaultVBox();

        configurationListView.setItems(FXCollections.observableArrayList("stopwatch.dayStatistic.default", "dates.fastEditButtons", "defaultRecords"));
        configurationListView.getSelectionModel().selectionProperty()
                .addListener((MapChangeListener<Integer, String>) change -> {
                            if (change.wasAdded()) {
                                configurationVBox.getChildren().clear();

                                String valueAdded = change.getValueAdded();
                                switch (valueAdded) {
                                    case "defaultRecords" -> configurationVBox.getChildren().add(defaultRecordNamesWrapperVBox);
                                    case "stopwatch.dayStatistic.default" ->
                                            configurationVBox.getChildren().add(dayStatisticDefaultVBox);
                                    case "dates.fastEditButtons" ->
                                            configurationVBox.getChildren().add(fastEditButtonsConfigurationVBox);
                                    default -> configurationVBox.getChildren().add(new Text("TODO add edit node"));
                                }
                            }
                        }
                );

        configurationListView.getSelectionModel().selectIndex(0);
    }
}
