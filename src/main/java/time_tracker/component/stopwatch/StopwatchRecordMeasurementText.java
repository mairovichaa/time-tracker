package time_tracker.component.stopwatch;

import javafx.beans.binding.StringBinding;
import javafx.scene.text.Text;
import lombok.NonNull;
import time_tracker.Utils;
import time_tracker.model.StopwatchRecordMeasurement;

import static time_tracker.Constants.DATA_TIME_FORMATTER;

public class StopwatchRecordMeasurementText extends Text {

    private final static String TEXT_PATTERN = "%s -> %s = %s";
    private final static String TEXT_WITH_NOTE_PATTERN = "%s -> %s = %s (%s)";

    public StopwatchRecordMeasurementText(@NonNull final StopwatchRecordMeasurement measurement) {
        var stopwatchStringProperty = new StringBinding() {
            {
                super.bind(measurement.getStartedAtProperty(), measurement.getStoppedAtProperty(), measurement.getNoteProperty());
            }

            @Override
            protected String computeValue() {
                var startedAt = measurement.getStartedAtProperty().getValue();
                var formattedStartedAt = DATA_TIME_FORMATTER.format(startedAt);
                var stoppedAt = measurement.getStoppedAtProperty().getValue();
                var formattedStoppedAt = DATA_TIME_FORMATTER.format(stoppedAt);

                var duration = measurement.getDuration();
                var formattedDuration = Utils.formatDuration(duration);

                var note = measurement.getNoteProperty()
                        .getValue();

                return note.isBlank() ?
                        String.format(TEXT_PATTERN, formattedStartedAt, formattedStoppedAt, formattedDuration) :
                        String.format(TEXT_WITH_NOTE_PATTERN, formattedStartedAt, formattedStoppedAt, formattedDuration, note);
            }
        };
        this.textProperty()
                .bind(stopwatchStringProperty);
    }

}
