package time_tracker.component.configuration;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import time_tracker.common.annotation.NonNull;
import time_tracker.component.configuration.dates.fastEditButtons.FastEditButtonsConfigurationVBox;
import time_tracker.component.configuration.defaultRecordNames.DefaultRecordConfigurationVBox;

import static time_tracker.component.Utils.load;

public class ConfigurationMenuVBox extends VBox {

    @FXML
    protected ListView<String> configurationListView;

    public ConfigurationMenuVBox() {
        load("/fxml/configuration/ConfigurationMenuVBox.fxml", this);
    }

    public void init(@NonNull final VBox configurationVBox) {
        var defaultRecordNamesWrapperVBox = new DefaultRecordConfigurationVBox();
        var fastEditButtonsConfigurationVBox = new FastEditButtonsConfigurationVBox();

        configurationListView.getItems().setAll("dates.fastEditButtons", "defaultRecords");
        configurationListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            configurationVBox.getChildren().clear();
            if (newValue.equals("defaultRecords")) {
                configurationVBox.getChildren().add(defaultRecordNamesWrapperVBox);
            }
            if (newValue.equals("dates.fastEditButtons")) {
                configurationVBox.getChildren().add(fastEditButtonsConfigurationVBox);
            } else {
                configurationVBox.getChildren().add(new Text("TODO add edit node"));
            }
        });
        configurationListView.getSelectionModel().select(1);
    }
}
