package time_tracker;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.time.Duration;

@UtilityClass
public class Utils {

    public String formatDuration(@NonNull final Duration duration) {
        var s = duration.getSeconds();
        return formatDuration(s);
    }
    public String formatDuration(final long seconds) {
        return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, (seconds % 60));
    }
}
