package time_tracker.component.stopwatch;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.common.GlobalContext;
import time_tracker.config.properties.StopwatchProperties;

import java.util.logging.Level;

import static time_tracker.component.Utils.load;

@Log
public class StopWatchVBox extends VBox {

    @FXML
    private DevVBox devVBox;

    public StopWatchVBox() {
        load("/fxml/stopwatch/StopWatchVBox.fxml", this);
        log.log(Level.FINE, "Create StopWatchTab");

        var stopwatchProperties = GlobalContext.get(StopwatchProperties.class);
        if (!stopwatchProperties.isDevMode()) {
            devVBox.setManaged(false);
            devVBox.setVisible(false);
        }
    }
}
