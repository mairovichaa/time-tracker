package time_tracker;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static time_tracker.Launcher.readLoggingConfiguration;

public class LoggingExtension implements BeforeAllCallback {

    private static boolean started = false;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (!started) {
            started = true;
            readLoggingConfiguration();
        }
    }
}
