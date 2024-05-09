package time_tracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.component.configuration.defaultRecordNames.DefaultRecordEntryVBox;
import time_tracker.config.properties.AppProperties;
import time_tracker.config.properties.StartProperties;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.config.properties.StopwatchProperties.FastEditButtonProperties;
import time_tracker.model.configuration.ConfigurationDefaultRecordModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static java.lang.String.format;

@Log
@Getter
public class ConfigurationService {

    private final ObservableList<ConfigurationDefaultRecordModel> configurationDefaultRecords = FXCollections.observableArrayList();
    private final ObservableList<FastEditButtonProperties> fastEditButtons = FXCollections.observableArrayList();

    private final AppProperties appProperties;
    private final String pathToPropertiesFile;
    private final ObjectMapper yamlObjectMapper;

    public ConfigurationService(
            @NonNull final AppProperties appProperties,
            @NonNull final StartProperties startProperties,
            @NonNull final ObjectMapper yamlObjectMapper) {
        this.appProperties = appProperties;
        this.pathToPropertiesFile = startProperties.getPathToPropertiesFile();
        this.yamlObjectMapper = yamlObjectMapper;

        appProperties.getStopwatch()
                .getDefaultRecords()
                .stream()
                .map(this::map)
                .forEach(configurationDefaultRecords::add);
        fastEditButtons.addAll(appProperties.getStopwatch().getDates().getFastEditButtons());
    }

    public void addFastEditButton(@NonNull final FastEditButtonProperties properties) {
        log.fine(() -> format("Add '%s' to fast edit button set", properties));

        StopwatchProperties appPropertiesStopwatch = appProperties.getStopwatch();
        List<FastEditButtonProperties> fastEditButtonsProps = appPropertiesStopwatch.getDates().getFastEditButtons();
        fastEditButtonsProps.add(properties);

        updatePropertiesFile(appProperties);

        fastEditButtons.add(properties);
    }

    public void deleteFastEditButton(@NonNull final FastEditButtonProperties properties) {
        log.fine(() -> format("Delete '%s' from fast edit button set", properties));
        StopwatchProperties appPropertiesStopwatch = appProperties.getStopwatch();
        List<FastEditButtonProperties> fastEditButtonsProps = appPropertiesStopwatch.getDates().getFastEditButtons();
        fastEditButtonsProps.remove(properties);

        updatePropertiesFile(appProperties);

        fastEditButtons.remove(properties);
    }

    public void addDefaultRecord(final String recordName) {
        log.fine(() -> format("Add '%s' to default record names set", recordName));

        StopwatchProperties appPropertiesStopwatch = appProperties.getStopwatch();
        List<StopwatchProperties.ConfigurationDefaultRecord> defaultRecords = appPropertiesStopwatch.getDefaultRecords();
        StopwatchProperties.ConfigurationDefaultRecord record = new StopwatchProperties.ConfigurationDefaultRecord();
        record.setName(recordName);
        record.setDisplay(DefaultRecordEntryVBox.DisplayOption.ALWAYS);
        defaultRecords.add(record);

        updatePropertiesFile(appProperties);

        ConfigurationDefaultRecordModel configurationDefaultRecordModel = this.map(record);
        configurationDefaultRecords.add(configurationDefaultRecordModel);
    }

    // TODO use ConfigurationDefaultRecordModel
    public void deleteDefaultRecord(final String recordName) {
        log.fine(() -> format("Delete '%s' from default record names set", recordName));
        StopwatchProperties appPropertiesStopwatch = appProperties.getStopwatch();
        List<StopwatchProperties.ConfigurationDefaultRecord> defaultRecords = appPropertiesStopwatch.getDefaultRecords();
        StopwatchProperties.ConfigurationDefaultRecord record = defaultRecords.stream()
                .filter(it -> it.getName().equals(recordName))
                .findFirst()
                .orElseThrow();
        defaultRecords.remove(record);
        updatePropertiesFile(appProperties);

        ConfigurationDefaultRecordModel foundConfigurationDefaultRecord = findConfigurationDefaultRecordModelByName(recordName);
        configurationDefaultRecords.remove(foundConfigurationDefaultRecord);
    }

    public void changeRecordDisplay(final String recordName, final DefaultRecordEntryVBox.DisplayOption option) {
        log.fine(() -> format("Change display option of default record '%s' to '%s", recordName, option));
        StopwatchProperties appPropertiesStopwatch = appProperties.getStopwatch();
        List<StopwatchProperties.ConfigurationDefaultRecord> defaultRecords = appPropertiesStopwatch.getDefaultRecords();
        defaultRecords.stream()
                .filter(it -> it.getName().equals(recordName))
                .findFirst()
                .orElseThrow()
                .setDisplay(option);

        updatePropertiesFile(appProperties);
        findConfigurationDefaultRecordModelByName(recordName).setDisplay(option);
    }

    public void updateStopwatchDayStatisticDefaultProperties(@NonNull final String durationInConfigsFormat, @NonNull final String comment) {
        StopwatchProperties.DefaultDayStatisticProperties defaultData = appProperties.getStopwatch().getStopwatch().getDayStatistic().getDefaultData();
        defaultData.setExpectedWorkTime(durationInConfigsFormat);
        defaultData.setComment(comment);
        updatePropertiesFile(appProperties);
    }

    private void updatePropertiesFile(@NonNull final AppProperties appProperties) {
        try {
            String newProps = yamlObjectMapper.writeValueAsString(appProperties);
            Files.writeString(Paths.get(pathToPropertiesFile), newProps);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
