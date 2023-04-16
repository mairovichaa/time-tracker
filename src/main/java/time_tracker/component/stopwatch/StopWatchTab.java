package time_tracker.component.stopwatch;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.component.stopwatch.date.FastEditButtonsVBox;
import time_tracker.component.stopwatch.measurement.ListOfMeasurementsForChosenRecordVBox;
import time_tracker.component.stopwatch.record.CreateRecordVBox;
import time_tracker.component.stopwatch.record.ListOfRecordsForChosenDateVBox;
import time_tracker.config.GlobalContext;
import time_tracker.config.properties.StopwatchProperties;

import java.util.logging.Level;

@Log
public class StopWatchTab extends Tab {
    public StopWatchTab() {
        super("Stopwatch");
        log.log(Level.FINE, "Create StopWatchTab");

        var wrapperVBox = new VBox();
        var stopwatchDateStatisticVBox = new StopwatchDateStatisticVBox();
        var createRecordVBox = new CreateRecordVBox();
        var fastEditButtonsVBox = new FastEditButtonsVBox();
        wrapperVBox.setSpacing(10);
        wrapperVBox.getChildren().addAll(stopwatchDateStatisticVBox, fastEditButtonsVBox, createRecordVBox);

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
        var scrollPane = new MFXScrollPane(hBoxWrapper);
        this.setContent(scrollPane);
    }
}
