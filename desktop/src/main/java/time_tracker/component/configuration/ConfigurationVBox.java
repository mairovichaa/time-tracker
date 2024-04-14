package time_tracker.component.configuration;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import static time_tracker.component.Utils.load;

public class ConfigurationVBox extends VBox {

    @FXML
    private ConfigurationMenuVBox configurationMenuVBox;
    @FXML
    private VBox wrapperVBox;

    public ConfigurationVBox() {
        load("/fxml/configuration/ConfigurationVBox.fxml", this);
        configurationMenuVBox.init(wrapperVBox);
    }
}
