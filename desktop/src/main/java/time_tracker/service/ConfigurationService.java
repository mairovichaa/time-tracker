package time_tracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.java.Log;
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

    public ConfigurationService(AppProperties appProperties, String pathToPropertiesFile) {
        this.appProperties = appProperties;
        this.pathToPropertiesFile = pathToPropertiesFile;

        defaultRecordNames.addAll(appProperties.getStopwatch().getDefaultRecords());
    }


    public void addDefaultRecord(final String recordName) {
        log.fine(() -> format("Add '%s' to default record names set", recordName));

        StopwatchProperties appPropertiesStopwatch = appProperties.getStopwatch();
        List<String> defaultRecords = appPropertiesStopwatch.getDefaultRecords();
        defaultRecords.add(recordName);

        // TODO get rid of duplication
        var objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            String newProps = objectMapper.writeValueAsString(appProperties);
            Files.writeString(Paths.get(pathToPropertiesFile), newProps);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        defaultRecordNames.add(recordName);
    }

    public void deleteDefaultRecord(final String recordName) {
        log.fine(() -> format("Delete '%s' from default record names set", recordName));
        StopwatchProperties appPropertiesStopwatch = appProperties.getStopwatch();
        List<String> defaultRecords = appPropertiesStopwatch.getDefaultRecords();
        defaultRecords.remove(recordName);

        var objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            String newProps = objectMapper.writeValueAsString(appProperties);
            Files.writeString(Paths.get(pathToPropertiesFile), newProps);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        defaultRecordNames.remove(recordName);
    }
}
