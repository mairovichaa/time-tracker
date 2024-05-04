package time_tracker.config.properties;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StartProperties {
    @Getter
    private final String pathToPropertiesFile;
}
