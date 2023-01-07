package time_tracker.service;

import lombok.extern.java.Log;
import time_tracker.domain.DayStatistics;
import time_tracker.model.DayData;
import time_tracker.model.StopWatchAppState;
import time_tracker.repository.DayStatisticsRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Log
public class DefaultDayStatisticsService implements DayStatisticsService {

    private final StopWatchAppState stopWatchAppState;
    private final DayStatisticsRepository dayStatisticsRepository;

    private final List<DayStatistics> data;

    public DefaultDayStatisticsService(StopWatchAppState stopWatchAppState, DayStatisticsRepository dayStatisticsRepository) {
        this.stopWatchAppState = stopWatchAppState;
        this.dayStatisticsRepository = dayStatisticsRepository;
        data = dayStatisticsRepository.findAll();
    }

    @Override
    public void save() {
        log.finest(() -> "save()");
        var chosenDate = stopWatchAppState.getChosenDate();
        var dayData = stopWatchAppState.getDateToDayData().get(chosenDate);

        data.stream()
                .filter(it -> it.getDate().equals(chosenDate))
                .findFirst()
                .ifPresentOrElse(
                        dayStatistics -> {
                            dayStatistics.setExpectedTotalInSecs(dayData.getExpectedTotalInSecsProperty().getValue());
                            dayStatistics.setNote(dayData.getNote());
                        },
                        () -> {
                            var dayStatistics = new DayStatistics();
                            dayStatistics.setDate(dayData.getDate());
                            dayStatistics.setExpectedTotalInSecs(dayData.getExpectedTotalInSecsProperty().getValue());
                            dayStatistics.setNote(dayData.getNote());
                            data.add(dayStatistics);
                        }
                );

        dayStatisticsRepository.save(data);
    }

    // TODO move to configs?
    private final static Long DEFAULT_EXPECTED_TOTAL_IN_SECS = (6 * 60 + 40L) * 60;
    @Override
    public void enrich(Map<LocalDate, DayData> dateToDayData) {
        data.forEach(it -> {
            var dayData = dateToDayData.get(it.getDate());
            if (dayData != null) {
                dayData.setId(it.getId());
                dayData.setNote(it.getNote());
                dayData.getExpectedTotalInSecsProperty().setValue(it.getExpectedTotalInSecs());
            }
        });
        dateToDayData.values()
                .stream()
                .filter(it -> !it.isExpectedTotalInSecsInitialized())
                .forEach(it -> it.getExpectedTotalInSecsProperty().setValue(DEFAULT_EXPECTED_TOTAL_IN_SECS));
    }
}
