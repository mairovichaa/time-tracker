package time_tracker.component.configuration;

import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.cell.MFXListCell;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import time_tracker.common.annotation.NonNull;
import time_tracker.component.configuration.dates.fastEditButtons.FastEditButtonsConfigurationVBox;
import time_tracker.component.configuration.defaultRecordNames.DefaultRecordConfigurationVBox;
import time_tracker.component.configuration.stopwatch.DayStatisticDefaultVBox;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

        List<String> defaultRecordsSorted = Arrays.asList("stopwatch.dayStatistic.default", "dates.fastEditButtons", "defaultRecords");
        Collections.sort(defaultRecordsSorted);
        ObservableList<String> defaultRecords = FXCollections.observableArrayList(defaultRecordsSorted);
        configurationListView.setItems(defaultRecords);
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
        configurationListView.setCellFactory(item ->
        {
            if (item.equals(defaultRecords.get(0))) {
                return new MFXListCell<>(configurationListView, item) {
                    @Override
                    // Properties
                    protected void initialize() {
                        super.initialize();
                        getStyleClass().add("mfx-list-cell-first");
                    }
                };
            }

            int lastIndex = defaultRecords.size() - 1;
            if (item.equals(defaultRecords.get(lastIndex))) {
                return new MFXListCell<>(configurationListView, item) {
                    @Override
                    // Properties
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
