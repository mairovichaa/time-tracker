package time_tracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.common.di.Bean;
import time_tracker.config.properties.AppProperties;
import time_tracker.config.properties.StartProperties;
import time_tracker.controller.configuration.ConfigurationController;
import time_tracker.model.StopWatchAppState;
import time_tracker.service.ConfigurationService;

import java.util.logging.Level;

@Log
public class ConfigurationConfiguration {

    @NonNull
    @Bean
    public ConfigurationService configurationService(
            @NonNull final AppProperties appProperties,
            @NonNull final StartProperties startProperties
    ) {
        log.log(Level.FINE, "Creating configurationService");
        // TODO create dedicated ObjectMapper for yaml - need qualifier support
        var yamlObjectMapper = new ObjectMapper(new YAMLFactory());
        return new ConfigurationService(appProperties, startProperties, yamlObjectMapper);
    }

    @NonNull
    @Bean
    public ConfigurationController configurationController(
            @NonNull final AppProperties appProperties,
            @NonNull final StopWatchAppState stopwatchAppState,
            @NonNull final ConfigurationService configurationService

    ) {
        log.log(Level.FINE, "Creating configurationController");
        return new ConfigurationController(appProperties, stopwatchAppState, configurationService);
    }
}