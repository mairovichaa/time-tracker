package time_tracker;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class Launcher {

    public static void main(String[] args) {
        readLoggingConfiguration();

        TimeTrackerApp.main(args);
    }

    public static void readLoggingConfiguration() {
        var resourceAsStream = Launcher.class.getResourceAsStream("/logging.properties");
        try (InputStream is = new BufferedInputStream(resourceAsStream)) {
            LogManager.getLogManager().readConfiguration(is);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
