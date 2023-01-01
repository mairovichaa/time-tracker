package time_tracker.component.stopwatch;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.config.GlobalContext;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopWatchAppState;

import java.util.logging.Level;

@Log
public class StopWatchTab extends Tab {
    public StopWatchTab() {
        super("Stopwatch");
        log.log(Level.FINE, "Create StopWatchTab");

        var wrapperVBox = new VBox();
        var stopwatchDateStatisticVBox = new StopwatchDateStatisticVBox();
        var createRecordVBox = new CreateRecordVBox();
        wrapperVBox.setSpacing(10);
        wrapperVBox.getChildren().addAll(stopwatchDateStatisticVBox, createRecordVBox);

        var stopwatchProperties = GlobalContext.get(StopwatchProperties.class);
        if (stopwatchProperties.isDevMode()) {
            var devVBox = new DevVBox();
            wrapperVBox.getChildren().add(devVBox);
        }

        var stopwatchPanelVBox = new ListOfRecordsForChosenDateVBox();

        var listOfDatesVbox = new ListOfDatesVbox();

        var listOfMeasurementsForChosenRecordVBox = new ListOfMeasurementsForChosenRecordVBox();

        var hBoxWrapper = new HBox();
        hBoxWrapper.setSpacing(10);

        hBoxWrapper.getChildren()
                .addAll(
                        listOfDatesVbox,
                        wrapperVBox,
                        stopwatchPanelVBox,
                        listOfMeasurementsForChosenRecordVBox
                );
        hBoxWrapper.setPadding(new Insets(10, 0, 0, 10));
        this.setContent(hBoxWrapper);
    }
}
