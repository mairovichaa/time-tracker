package time_tracker.component.stopwatch;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.LongExpression;
import javafx.beans.binding.StringBinding;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.NonNull;
import time_tracker.Utils;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;
import time_tracker.service.StopwatchRecordService;

import java.util.stream.Collectors;

public class StopwatchRecordVBox extends VBox {

    private final static String START_BUTTON_TEXT_VALUE = "Start";
    private final static String STOP_BUTTON_TEXT_VALUE = "Stop";
    private final static String DELIMITER_TEXT_VALUE = "-------";
    public static final String TOTAL_TIME_INITIAL_TEXT_VALUE = "00:00:00";
    @Getter
    private final String name;
    private final StopwatchRecord stopwatchRecord;

    private final Text stopwatchText = new Text();
    private final Text stopwatchTotalTimeText = new Text(TOTAL_TIME_INITIAL_TEXT_VALUE);

    private final Button stopwatchStartButton = new Button(START_BUTTON_TEXT_VALUE);
    private final Button stopwatchStopButton = new Button(STOP_BUTTON_TEXT_VALUE);

    private final HBox stopwatchButtonsWrapper = new HBox(stopwatchStartButton, stopwatchStopButton);
    private final Text separatorText = new Text(DELIMITER_TEXT_VALUE);

    public StopwatchRecordVBox(
            @NonNull final StopwatchRecord stopwatchRecord,
            @NonNull final StopwatchRecordService stopwatchRecordService
    ) {
        System.out.println("Create StopwatchRecord");
        this.name = stopwatchRecord.getName();
        this.stopwatchRecord = stopwatchRecord;

        stopwatchText.setText(this.name);
        stopwatchStartButton.disableProperty()
                .bind(stopwatchRecord.getHasMeasurementInProgress());
        stopwatchStopButton.disableProperty()
                .bind(Bindings.not(stopwatchRecord.getHasMeasurementInProgress()));

        stopwatchStartButton.setOnMouseClicked(e -> {
            System.out.println("stopwatchStartButton is clicked");
            stopwatchRecordService.startNewMeasurement(stopwatchRecord);
            rebindTotalTimeCalc();
            redrawMeasurements();
        });

        stopwatchStopButton.setOnMouseClicked(e -> {
            System.out.println("stopwatchStopButton is clicked");
            stopwatchRecordService.stopMeasurement(stopwatchRecord);
            redrawMeasurements();
        });
        rebindTotalTimeCalc();
        redrawMeasurements();
    }

    private void rebindTotalTimeCalc() {
        stopwatchTotalTimeText.textProperty().unbind();
        StringBinding longBinding = new StringBinding() {
            {
                var collect = stopwatchRecord.getMeasurementsProperty().stream()
                        .map(StopwatchRecordMeasurement::getDurationProperty)
                        .collect(Collectors.toList());

                if (stopwatchRecord.getMeasurementInProgress() != null) {
                    collect.add(stopwatchRecord.getMeasurementInProgress().getDurationProperty());
                }

                super.bind(collect.toArray(new Observable[]{}));
            }

            @Override
            protected String computeValue() {
                var totalSeconds = stopwatchRecord.getMeasurementsProperty().stream()
                        .map(StopwatchRecordMeasurement::getDurationProperty)
                        .mapToLong(LongExpression::get)
                        .sum();

                if (stopwatchRecord.getMeasurementInProgress() != null) {
                    totalSeconds += stopwatchRecord.getMeasurementInProgress().getDurationProperty().get();
                }
                return Utils.formatDuration(totalSeconds);
            }
        };
        stopwatchTotalTimeText.textProperty()
                .bind(longBinding);
    }

    private void redrawMeasurements() {
        var children = this.getChildren();
        children.clear();
        children.addAll(stopwatchText, stopwatchTotalTimeText);

        if (!stopwatchRecord.getMeasurementsProperty().isEmpty()) {
            stopwatchRecord.getMeasurementsProperty()
                    .stream()
                    .map(StopwatchRecordMeasurementText::new)
                    .forEach(children::add);
        }

        var measurementInProgress = stopwatchRecord.getMeasurementInProgress();
        if (measurementInProgress != null) {
            var hBox = new StopwatchRecordMeasurementInProgressVBox(measurementInProgress);
            children.add(hBox);
        }

        children.addAll(stopwatchButtonsWrapper, separatorText);
    }

}
