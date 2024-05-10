package time_tracker.component.configuration.dates.fastEditButtons;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.component.common.Icon;
import time_tracker.config.properties.StopwatchProperties.FastEditButtonProperties;
import time_tracker.controller.configuration.ConfigurationController;

import java.util.List;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;
import static time_tracker.component.common.Confirmation.requireConfirmation;
import static time_tracker.component.common.IconButton.initIconButton;

@Log
public class FastEditButtonEntryPane extends Pane {

    @FXML
    private Text fastButtonNameText;
    @FXML
    private Label fastButtonTimeLabel;
    @FXML
    private Button deleteButton;
    @FXML
    private Label expectedIconLabel;

    private final FastEditButtonProperties properties;

    public FastEditButtonEntryPane(@NonNull final FastEditButtonProperties properties) {
        load("/fxml/configuration/dates/fastEditButtons/FastEditButtonEntryPane.fxml", this);
        this.properties = properties;

        initIconButton(deleteButton, 15, Icon.DELETE);
        initIconButton(expectedIconLabel, 16, Icon.SCHEDULE, List.of("icon-label-black"), List.of("label-icon-black"));

        fastButtonNameText.setText(properties.getName());
        String formattedTime = properties.getExpected().toLowerCase();
        fastButtonTimeLabel.setText(formattedTime);
    }

    @FXML
    protected void delete() {
        log.info(() -> "'deleteButton' is clicked");
        requireConfirmation().whenComplete((it, ex) -> {
            if (it) {
                var configurationController = CONTEXT.get(ConfigurationController.class);
                configurationController.deleteFastEditButton(properties);
            } else {
                log.fine(() -> "'No' button is clicked");
            }
        });
    }
}
