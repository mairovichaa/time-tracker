package time_tracker.model.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ConfigurationItem {
    DATES_FAST_EDIT_BUTTONS("dates.fastEditButtons"),
    DEFAULT_RECORDS("defaultRecords"),
    STOPWATCH_DAY_STATISTIC_DEFAULT("stopwatch.dayStatistic.default");

    private final String name;
}
