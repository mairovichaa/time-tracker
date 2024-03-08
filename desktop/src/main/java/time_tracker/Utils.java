package time_tracker;

import lombok.experimental.UtilityClass;
import time_tracker.common.annotation.NonNull;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class Utils {
    public static final DateTimeFormatter DATE_FORMAT_WITH_SHORT_DAY_NAME = DateTimeFormatter.ofPattern("yyyy.MM.dd (EEE)");

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public String formatDuration(@NonNull final Duration duration) {
        var s = duration.getSeconds();
        return formatDuration(s);
    }

    public String formatDuration(final long seconds) {
        return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, (seconds % 60));
//        var result = new StringBuilder();
//        long hours = seconds / 3600;
//        if (hours != 0) {
//            result.append(hours).append("h ");
//        }
//
//        long minutesInLastHour = (seconds % 3600) / 60;
//        if (hours != 0 || minutesInLastHour != 0) {
//            result.append(minutesInLastHour).append("m ");
//        }
//
//        long secondsInLastMinute = seconds % 60;
//        result.append(secondsInLastMinute).append("s");
//
//        return result.toString();
    }
    public String formatLocalDate(@NonNull final LocalDate date) {
        return DATE_FORMAT.format(date);
    }

    @NonNull
    public LocalDate parseLocalDate(@NonNull final String date, @NonNull final DateTimeFormatter formatter) {
        return formatter.parse(date, LocalDate::from);
    }
}
