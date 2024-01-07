package time_tracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.config.properties.AppProperties;
import time_tracker.config.properties.StopwatchProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static java.lang.String.format;

@Log
@Getter
public class ConfigurationService {

    private final ObservableList<String> defaultRecordNames = FXCollections.observableArrayList();

    private final AppProperties appProperties;
    private final String pathToPropertiesFile;
    private final ObjectMapper yamlObjectMapper;

    public ConfigurationService(
            @NonNull final AppProperties appProperties,
            @NonNull final String pathToPropertiesFile,
            @NonNull final ObjectMapper yamlObjectMapper) {
        this.appProperties = appProperties;
        this.pathToPropertiesFile = pathToPropertiesFile;
        this.yamlObjectMapper = yamlObjectMapper;

        defaultRecordNames.addAll(appProperties.getStopwatch().getDefaultRecords());
    }

    public void addDefaultRecord(final String recordName) {
        log.fine(() -> format("Add '%s' to default record names set", recordName));

        StopwatchProperties appPropertiesStopwatch = appProperties.getStopwatch();
        List<String> defaultRecords = appPropertiesStopwatch.getDefaultRecords();
        defaultRecords.add(recordName);

        updatePropertiesFile(appProperties);

        defaultRecordNames.add(recordName);
    }

    public void deleteDefaultRecord(final String recordName) {
        log.fine(() -> format("Delete '%s' from default record names set", recordName));
        StopwatchProperties appPropertiesStopwatch = appProperties.getStopwatch();
        List<String> defaultRecords = appPropertiesStopwatch.getDefaultRecords();
        defaultRecords.remove(recordName);

        updatePropertiesFile(appProperties);

        defaultRecordNames.remove(recordName);
    }

    private void updatePropertiesFile(@NonNull final AppProperties appProperties) {
        try {
            String newProps = yamlObjectMapper.writeValueAsString(appProperties);
            Files.writeString(Paths.get(pathToPropertiesFile), newProps);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
