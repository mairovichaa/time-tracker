package time_tracker.component.configuration;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import time_tracker.component.configuration.dates.fastEditButtons.FastEditButtonsConfigurationVBox;
import time_tracker.component.configuration.defaultRecordNames.DefaultRecordConfigurationVBox;
import time_tracker.component.configuration.stopwatch.DayStatisticDefaultVBox;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.configuration.ConfigurationState;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.*;

public class ConfigurationVBox extends VBox {

    @FXML
    private FastEditButtonsConfigurationVBox fastEditButtonsConfigurationVBox;
    @FXML
    private DefaultRecordConfigurationVBox defaultRecordConfigurationVBox;
    @FXML
    private DayStatisticDefaultVBox dayStatisticDefaultVBox;

    public ConfigurationVBox() {
        load("/fxml/configuration/ConfigurationVBox.fxml", this);

        StopWatchAppState stopwatchAppState = CONTEXT.get(StopWatchAppState.class);
        ConfigurationState configurationState = stopwatchAppState.getConfigurationState();
        configurationState.getChosenItem()
                .addListener((observable, oldValue, newValue) -> {
                    if (oldValue != null) {
                        switch (oldValue) {
                            case DATES_FAST_EDIT_BUTTONS -> hide(fastEditButtonsConfigurationVBox);
                            case DEFAULT_RECORDS -> hide(defaultRecordConfigurationVBox);
                            case STOPWATCH_DAY_STATISTIC_DEFAULT -> hide(dayStatisticDefaultVBox);
                        }
                    }
                    switch (newValue) {
                        case DATES_FAST_EDIT_BUTTONS -> show(fastEditButtonsConfigurationVBox);
                        case DEFAULT_RECORDS -> show(defaultRecordConfigurationVBox);
                        case STOPWATCH_DAY_STATISTIC_DEFAULT -> show(dayStatisticDefaultVBox);
                    }
                });
    }
}
