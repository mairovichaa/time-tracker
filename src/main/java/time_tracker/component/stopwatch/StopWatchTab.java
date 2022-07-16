package time_tracker.component.stopwatch;

import javafx.geometry.Orientation;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.component.stopwatch.factory.StopwatchDateStatisticVBoxFactory;
import time_tracker.component.stopwatch.factory.StopwatchDatesVboxFactory;
import time_tracker.component.stopwatch.factory.StopwatchPanelVBoxFactory;

import java.util.logging.Level;

@Log
public class StopWatchTab extends Tab {
    public StopWatchTab(
            @NonNull final StopwatchDatesVboxFactory stopwatchDatesVboxFactory,
            @NonNull final StopwatchPanelVBoxFactory stopwatchPanelVBoxFactory,
            @NonNull final StopwatchDateStatisticVBoxFactory stopwatchDateStatisticVBoxFactory
    ) {
        super("Stopwatch");
        log.log(Level.FINE, "Create StopWatchTab");

        var stopwatchDatesVbox = stopwatchDatesVboxFactory.create();
        var stopwatchPanelVBox = stopwatchPanelVBoxFactory.create();
        var stopwatchDateStatisticVBox = stopwatchDateStatisticVBoxFactory.create();

        var scrollPane = new ScrollPane(stopwatchPanelVBox);

        var hBoxWrapper = new HBox();

        hBoxWrapper.getChildren()
                .addAll(
                        stopwatchDatesVbox,
                        new Separator(Orientation.VERTICAL),
                        stopwatchDateStatisticVBox,
                        scrollPane
                );

        this.setContent(hBoxWrapper);
    }
}
