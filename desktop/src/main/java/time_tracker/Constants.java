package time_tracker;

import java.time.format.DateTimeFormatter;

// TODO use UtilityClass
public class Constants {

    public static final int AMOUNT_OF_RECORDS_TO_SHOW_IN_A_BATCH = 10;
    public static final double THRESHOLD_TO_LOAD_NEXT_BATCH = 0.75;

    public static final DateTimeFormatter DATA_TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm:ss");
    // TODO merge two FORMATTERS
    public static final DateTimeFormatter DATA_TIME_FORMATTER_2 = DateTimeFormatter.ofPattern("HH:mm:ss");

}
