package time_tracker.component.stopwatch;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import time_tracker.annotation.NonNull;
import time_tracker.component.Utils;
import time_tracker.config.GlobalContext;
import time_tracker.service.dev.RandomStopwatchRecordFactory;

public class DevVBox extends VBox {


    @NonNull
    private final RandomStopwatchRecordFactory randomStopwatchRecordFactory;

    public DevVBox() {
        Utils.load("/fxml/stopwatch/DevVBox.fxml", this);
        this.randomStopwatchRecordFactory = GlobalContext.get(RandomStopwatchRecordFactory.class);
    }

    @FXML
    private void generateRandom() {
        randomStopwatchRecordFactory.create();
    }
}
