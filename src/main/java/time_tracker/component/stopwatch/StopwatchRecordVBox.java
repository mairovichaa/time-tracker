package time_tracker.component.stopwatch;

import javafx.beans.Observable;
import javafx.beans.binding.LongExpression;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.NonNull;
import time_tracker.Utils;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class StopwatchRecordVBox extends VBox {

    private final static String START_BUTTON_TEXT_VALUE = "Start";
    private final static String STOP_BUTTON_TEXT_VALUE = "Stop";
    private final static String DELIMITER_TEXT_VALUE = "-------";
    public static final String TOTAL_TIME_INITIAL_TEXT_VALUE = "00:00:00";
    @Getter
    private final ObservableList<StopwatchRecordMeasurementHBox> measurements = FXCollections.observableArrayList();
    @Getter
    private final String name;
    private final AtomicReference<StopwatchRecordMeasurementHBox> currentStopwatchEntry = new AtomicReference<>();

    public StopwatchRecordVBox(@NonNull final String name) {
        System.out.println("Create StopwatchRecord");
        this.name = name;
        var stopwatchText = new Text(name);
        var stopwatchTotalTimeText = new Text("0");

        var stopwatchStartButton = new Button(START_BUTTON_TEXT_VALUE);

        var stopwatchStopButton = new Button(STOP_BUTTON_TEXT_VALUE);
        stopwatchStopButton.setDisable(true);

        stopwatchStartButton.setOnMouseClicked(e -> {
            System.out.println("stopwatchStartButton is clicked");
            var stopwatchRecordMeasurement = new StopwatchRecordMeasurementHBox();
            measurements.add(stopwatchRecordMeasurement);

            stopwatchRecordMeasurement.start();
            currentStopwatchEntry.set(stopwatchRecordMeasurement);

            rebindTotalTimeCalc(stopwatchTotalTimeText);

            var children = this.getChildren();
            children.add(children.size() - 1, stopwatchRecordMeasurement);

            stopwatchStartButton.setDisable(true);
            stopwatchStopButton.setDisable(false);
        });

        stopwatchStopButton.setOnMouseClicked(e -> {
            System.out.println("stopwatchStopButton is clicked");

            currentStopwatchEntry.get().stop();
            currentStopwatchEntry.set(null);

            stopwatchStartButton.setDisable(false);
            stopwatchStopButton.setDisable(true);
        });


        var stopwatchButtonsWrapper = new HBox(stopwatchStartButton, stopwatchStopButton);
        var separatorText = new Text(DELIMITER_TEXT_VALUE);
        this.getChildren().addAll(stopwatchText, stopwatchTotalTimeText, stopwatchButtonsWrapper, separatorText);
    }

    private void rebindTotalTimeCalc(Text stopwatchTotalTimeText) {
        stopwatchTotalTimeText.textProperty().unbind();
        StringBinding longBinding = new StringBinding() {
            {
                var collect = measurements.stream()
                        .map(StopwatchRecordMeasurementHBox::getDurationProperty)
                        .collect(Collectors.toList());
                super.bind(collect.toArray(new Observable[]{}));
            }

            @Override
            protected String computeValue() {
                if (measurements.isEmpty()) {
                    return TOTAL_TIME_INITIAL_TEXT_VALUE;
                }
                var totalSeconds = measurements.stream()
                        .map(StopwatchRecordMeasurementHBox::getDurationProperty)
                        .mapToLong(LongExpression::get)
                        .sum();
                return Utils.formatDuration(totalSeconds);
            }
        };
        stopwatchTotalTimeText.textProperty()
                .bind(longBinding);
    }


}
