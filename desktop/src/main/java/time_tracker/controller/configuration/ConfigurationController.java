package time_tracker.controller.configuration;

import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.component.configuration.defaultRecordNames.DefaultRecordEntryVBox;
import time_tracker.config.properties.AppProperties;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.configuration.ConfigurationDefaultRecordModel;
import time_tracker.model.configuration.ConfigurationState;
import time_tracker.service.ConfigurationService;

import static java.lang.String.format;

@Log
@Getter
public class ConfigurationController {

    private final ConfigurationService configurationService;
    private final ConfigurationState configurationState;
    private final ObservableList<StopwatchProperties.FastEditButtonProperties> fastEditButtons;
    private final ObservableList<ConfigurationDefaultRecordModel> configurationDefaultRecords;

    public ConfigurationController(
            @NonNull final AppProperties appProperties,
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final ConfigurationService configurationService) {
        this.configurationState = stopWatchAppState.getConfigurationState();
        this.configurationService = configurationService;

        configurationDefaultRecords = configurationState.getConfigurationDefaultRecords();
        appProperties.getStopwatch()
                .getDefaultRecords()
                .stream()
                .map(this::map)
                .forEach(configurationDefaultRecords::add);

        fastEditButtons = configurationState.getFastEditButtons();
        fastEditButtons.addAll(appProperties.getStopwatch().getDates().getFastEditButtons());
    }

    public void addFastEditButton(@NonNull final StopwatchProperties.FastEditButtonProperties fastEditButtonProperties) {
        log.fine(() -> format("Add '%s' to fast edit button set", fastEditButtonProperties));
        configurationService.addFastEditButton(fastEditButtonProperties);
        fastEditButtons.add(fastEditButtonProperties);
    }

    public void deleteFastEditButton(@NonNull final StopwatchProperties.FastEditButtonProperties fastEditButtonProperties) {
        log.fine(() -> format("Delete '%s' from fast edit button set", fastEditButtonProperties));
        configurationService.deleteFastEditButton(fastEditButtonProperties);
        fastEditButtons.remove(fastEditButtonProperties);
    }

    public void addDefaultRecord(final String recordName) {
        log.fine(() -> format("Add '%s' to default record names set", recordName));
        configurationService.addDefaultRecord(recordName);

        var configurationDefaultRecordModel = new ConfigurationDefaultRecordModel();
        configurationDefaultRecordModel.setName(recordName);
        configurationDefaultRecordModel.setDisplay(DefaultRecordEntryVBox.DisplayOption.ALWAYS);
        configurationDefaultRecords.add(configurationDefaultRecordModel);
    }

    // TODO use ConfigurationDefaultRecordModel
    public void deleteDefaultRecord(final String recordName) {
        log.fine(() -> format("Delete '%s' from default record names set", recordName));
        configurationService.deleteDefaultRecord(recordName);

        ConfigurationDefaultRecordModel foundConfigurationDefaultRecord = findConfigurationDefaultRecordModelByName(recordName);
        configurationDefaultRecords.remove(foundConfigurationDefaultRecord);
    }

    public void changeRecordDisplay(final String recordName, final DefaultRecordEntryVBox.DisplayOption option) {
        log.fine(() -> format("Change display option of default record '%s' to '%s", recordName, option));
        configurationService.changeRecordDisplay(recordName, option);
        findConfigurationDefaultRecordModelByName(recordName).setDisplay(option);
    }

    public void updateStopwatchDayStatisticDefaultProperties(@NonNull final String durationInConfigsFormat, @NonNull final String comment) {
        log.fine(() -> format("Change stopwatch day statistic default: duration='%s', comment='%s'", durationInConfigsFormat, comment));
        configurationService.updateStopwatchDayStatisticDefaultProperties(durationInConfigsFormat, comment);
    }

    @NonNull
    private ConfigurationDefaultRecordModel map(@NonNull final StopwatchProperties.ConfigurationDefaultRecord src) {
        ConfigurationDefaultRecordModel result = new ConfigurationDefaultRecordModel();
        result.setName(src.getName());
        result.setDisplay(src.getDisplay());
        return result;
    }

    @NonNull
    private ConfigurationDefaultRecordModel findConfigurationDefaultRecordModelByName(@NonNull final String name) {
        return configurationDefaultRecords.stream()
                .filter(it -> it.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }
}
