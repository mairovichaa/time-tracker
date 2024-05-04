package time_tracker.service;

import javafx.collections.FXCollections;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.domain.DayStatistics;
import time_tracker.model.DayData;
import time_tracker.model.StopWatchAppState;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Log
@RequiredArgsConstructor
public class DayDataService {

    private final StopWatchAppState stopWatchAppState;
    private final StopwatchProperties.DefaultDayStatisticProperties defaultDayStatisticProperties;
    private final DayStatisticsService dayStatisticsService;
    private final StopwatchRecordService stopwatchRecordService;

    public void save(@NonNull final DayData dayData) {
        var dayStatistics = new DayStatistics();
        dayStatistics.setId(dayData.getId());
        dayStatistics.setDate(dayData.getDate());
        dayStatistics.setExpectedTotalInSecs(dayData.getExpected().toSeconds());
        dayStatistics.setNote(dayData.getNote());

        dayStatisticsService.save(dayStatistics);
        stopwatchRecordService.store(dayData.getDate());
    }

    @NonNull
    public Map<LocalDate, DayData> findAll() {
        var dateToDayData = stopWatchAppState.getDateToRecords()
                .entrySet()
                .stream()
                .map(it -> new DayData(it.getKey(), it.getValue()))
                .collect(Collectors.toMap(DayData::getDate, x -> x));

        dayStatisticsService.findAll()
                .forEach(it -> {
                    log.finest(() -> it.getDate().toString());
                    var date = it.getDate();
                    var dayData = dateToDayData.get(date);
                    dayData.setId(it.getId());
                    dayData.setNote(it.getNote());
                    dayData.getExpectedTotalInSecsProperty().setValue(it.getExpectedTotalInSecs());
                });

        String duration = defaultDayStatisticProperties.getExpectedWorkTime();
        String comment = defaultDayStatisticProperties.getComment();
        dateToDayData.values()
                .stream()
                .filter(it -> !it.isExpectedTotalInSecsInitialized())
                .forEach(it -> {
                    Duration durationParsed = Duration.parse("PT" + duration);
                    it.setExpected(durationParsed);
                    it.setNote(comment);
                });
        return dateToDayData;
    }

    public void create(@NonNull LocalDate now) {
        log.finest(() -> "create()");
        stopwatchRecordService.store(now);
        var dayStatistics = dayStatisticsService.create(now);

        var dayData = new DayData(now, FXCollections.observableArrayList());
        dayData.setId(dayStatistics.getId());
        dayData.setExpected(Duration.ofSeconds(0));

        stopWatchAppState.getDateToDayData()
                .put(now, dayData);
    }
}
