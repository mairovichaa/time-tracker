package time_tracker.service;

import javafx.collections.ObservableList;
import lombok.RequiredArgsConstructor;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;
import time_tracker.repository.StopwatchRecordRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static javafx.collections.FXCollections.observableArrayList;

@RequiredArgsConstructor
public class InitialDataLoadService {
    private final StopwatchRecordRepository stopwatchRecordRepository;
    private final StopWatchAppState stopWatchAppState;
    private final StopwatchRecordOnLoadFactory stopwatchRecordOnLoadFactory;
    private final DayDataService dayDataService;
    private final DayStatisticsService dayStatisticsService;

    public void load() {
        var chosenDateToRecordsForChosenDateBinder = new ChosenDateToRecordsForChosenDateBinder(stopWatchAppState, stopwatchRecordOnLoadFactory);
        chosenDateToRecordsForChosenDateBinder.bind();

        Map<LocalDate, ObservableList<StopwatchRecord>> dateToRecords = new HashMap<>();
        stopwatchRecordRepository.getLoaded()
                .forEach((date, records) -> dateToRecords.put(date, observableArrayList(records)));

        dayStatisticsService.findAll()
                .forEach(it -> {
                    if (!dateToRecords.containsKey(it.getDate())) {
                        dateToRecords.put(it.getDate(), observableArrayList());
                    }
                });

        stopWatchAppState.setDateToRecords(dateToRecords);

        var dayData = dayDataService.findAll();
        stopWatchAppState.setDateToDayData(dayData);
    }
}
