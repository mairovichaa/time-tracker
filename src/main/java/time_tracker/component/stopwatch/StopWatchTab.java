package time_tracker.component.stopwatch;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import time_tracker.annotation.NonNull;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopWatchAppState;
import time_tracker.repository.StopwatchRecordRepository;
import time_tracker.service.StopwatchRecordService;
import time_tracker.service.dev.RandomStopwatchRecordFactory;

public class StopWatchTab extends Tab {

    public StopWatchTab(
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final StopwatchRecordService stopwatchRecordService,
            @NonNull final StopwatchRecordRepository stopwatchRecordRepository,
            @NonNull final RandomStopwatchRecordFactory randomStopwatchRecordFactory,
            @NonNull final StopwatchProperties stopwatchProperties
    ) {
        super("Stopwatch");

        // TODO introduce a factory for it
        var stopwatchDatesProperties = stopwatchProperties.getDates();
        var stopwatchDatesVbox = new StopwatchDatesVbox(stopWatchAppState, stopwatchRecordService, stopwatchRecordRepository, stopwatchDatesProperties);

        // TODO introduce a factory for it
        var stopwatchPanelVBox = new StopwatchPanelVBox(stopwatchRecordService, randomStopwatchRecordFactory, stopwatchProperties);
        var scrollPane = new ScrollPane(stopwatchPanelVBox);

        var hBoxWrapper = new HBox();

        hBoxWrapper.getChildren()
                .addAll(stopwatchDatesVbox, scrollPane);

        this.setContent(hBoxWrapper);
    }


}
