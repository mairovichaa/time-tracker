package time_tracker.component.configuration.dates.fastEditButtons;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.config.properties.StopwatchProperties.FastEditButtonProperties;
import time_tracker.controller.configuration.ConfigurationController;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;

@Log
public class FastEditButtonEntryVBox extends VBox {

    @FXML
    private Text fastButtonNameText;
    @FXML
    private Text fastButtonTimeText;

    private final FastEditButtonProperties properties;

    public FastEditButtonEntryVBox(@NonNull final FastEditButtonProperties properties) {
        load("/fxml/configuration/dates/fastEditButtons/FastEditButtonEntryVBox.fxml", this);
        this.properties = properties;

        fastButtonNameText.setText(properties.getName());
        String formattedTime = properties.getExpected();
        fastButtonTimeText.setText(formattedTime);
    }

    @FXML
    protected void delete() {
        log.info(() -> "'deleteDefaultRecord' is clicked");
        var configurationController = CONTEXT.get(ConfigurationController.class);
        configurationController.deleteFastEditButton(properties);
    }

}
