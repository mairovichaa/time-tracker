package time_tracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.common.di.Bean;

import java.util.logging.Level;

@Log
public class CommonConfiguration {

    @NonNull
    @Bean
    public ObjectMapper objectMapper() {
        log.log(Level.FINE, "Creating objectMapper");
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

}
