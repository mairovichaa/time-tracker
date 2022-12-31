package time_tracker;

import java.time.format.DateTimeFormatter;

// TODO use UtilityClass
public class Constants {
    public static final DateTimeFormatter DATA_TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm:ss");
    // TODO merge two FORMATTERS
    public static final DateTimeFormatter DATA_TIME_FORMATTER_2 = DateTimeFormatter.ofPattern("HH:mm:ss");

}
