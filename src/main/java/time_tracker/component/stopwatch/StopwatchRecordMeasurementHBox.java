package time_tracker.component.stopwatch;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import lombok.Getter;
import time_tracker.Utils;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class StopwatchRecordMeasurementHBox extends HBox {
    private final static DateTimeFormatter DATA_TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm:ss");
    private final static long TIMER_PERIOD_MILLIS = 1000;

    private final Text startedAtText;
    private final StringProperty stoppedAtStringProperty;

    private final StringProperty diffStringProperty;

    private final Timer timer;

    private LocalTime startedAt;
    private LocalTime stoppedAt;

    @Getter
    private final SimpleLongProperty durationProperty = new SimpleLongProperty(0);

    public StopwatchRecordMeasurementHBox() {
        super();
        timer = new Timer();

        startedAtText = new Text();
        Text arrowText = new Text(" -> ");

        Text stoppedAtText = new Text();
        stoppedAtStringProperty = new SimpleStringProperty();
        stoppedAtText.textProperty().bind(stoppedAtStringProperty);

        var equalText = new Text(" = ");

        var diffText = new Text("00:00:00");
        diffStringProperty = new SimpleStringProperty();
        diffText.textProperty().bind(diffStringProperty);

        this.getChildren().addAll(startedAtText, arrowText, stoppedAtText, equalText, diffText);
    }

    public void start() {
        startedAt = LocalTime.now();
        System.out.println("measurement started at " + startedAt);
        var formatted = DATA_TIME_FORMATTER.format(startedAt);
        startedAtText.setText(formatted);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                stoppedAt = LocalTime.now();
                recalculateStoppedAtText();
                recalculateDurationTextAndDurationProperty();
            }
        };

        timer.scheduleAtFixedRate(
                timerTask,
                0,
                TIMER_PERIOD_MILLIS
        );
    }

    private void recalculateDurationTextAndDurationProperty() {
        var duration = Duration.between(startedAt, stoppedAt);
        var formatted = Utils.formatDuration(duration);
        diffStringProperty.set(formatted);
        durationProperty.set(duration.getSeconds());
    }

    private void recalculateStoppedAtText() {
        var formattedStoppedAt = DATA_TIME_FORMATTER.format(stoppedAt);
        stoppedAtStringProperty.set(formattedStoppedAt);
    }

    public void stop() {
        System.out.println("measurement stopped at " + stoppedAt);
        timer.cancel();
    }

    public String getMeasurementString() {
        return String.format("%s -> %s = %s",
                startedAtText.getText(),
                stoppedAtStringProperty.get(),
                diffStringProperty.get()
        );
    }
}
