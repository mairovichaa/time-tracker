package time_tracker.component.stopwatch;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import time_tracker.annotation.NonNull;
import time_tracker.model.StopWatchAppState;
import time_tracker.repository.StopwatchRecordRepository;
import time_tracker.service.StopwatchRecordService;

import java.time.LocalDate;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class StopwatchDatesVbox extends VBox {
    // TODO move to configs
    private static final int AMOUNT_OF_DAYS_TO_SHOW = 30;

    @NonNull
    private final StopwatchRecordService stopwatchRecordService;

    @NonNull
    private final StopwatchRecordRepository stopwatchRecordRepository;

    public StopwatchDatesVbox(
            @NonNull final StopWatchAppState stopWatchAppState,
            @NonNull final StopwatchRecordService stopwatchRecordService,
            @NonNull final StopwatchRecordRepository stopwatchRecordRepository
    ) {
        this.stopwatchRecordService = stopwatchRecordService;
        this.stopwatchRecordRepository = stopwatchRecordRepository;

        var datesText = new Text("Dates");

        var today = stopWatchAppState.getChosenDate();
        var texts = IntStream.range(0, AMOUNT_OF_DAYS_TO_SHOW)
                .mapToObj(today::minusDays)
                .map(date -> new StopwatchDateText(date, stopWatchAppState))
                .collect(toList());

        var children = this.getChildren();
        children.addAll(datesText);
        children.addAll(texts);

        loadAndSetStopwatchRecords(today);

        texts.forEach(it -> it.setOnMouseClicked(ignored -> {
            var chosenDate = it.getDate();
            stopWatchAppState.setChosenDate(chosenDate);

            loadAndSetStopwatchRecords(chosenDate);
        }));
    }

    private void loadAndSetStopwatchRecords(@NonNull final LocalDate chosenDate) {
        // TODO store current records before load of other
        // maybe it makes sense to disable loading if there is at least one running measurement
        var records = stopwatchRecordRepository.load(chosenDate);
        stopwatchRecordService.setRecords(records);
    }

}
