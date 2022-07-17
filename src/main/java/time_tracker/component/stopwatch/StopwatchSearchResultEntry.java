package time_tracker.component.stopwatch;

import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import time_tracker.Utils;
import time_tracker.annotation.NonNull;
import time_tracker.model.StopwatchRecord;


public class StopwatchSearchResultEntry extends VBox {

    public StopwatchSearchResultEntry(@NonNull final StopwatchRecord record) {
        var recordName = new Text(record.getName());
        var formattedDate = Utils.formatLocalDate(record.getDate());
        var dateText = new Text(formattedDate);

        var children = this.getChildren();
        children.addAll(recordName, dateText);

        if (!record.getMeasurementsProperty().isEmpty()) {
            record.getMeasurementsProperty()
                    .stream()
                    .map(StopwatchRecordMeasurementText::new)
                    .forEach(children::add);
        }

        var measurementInProgress = record.getMeasurementInProgress();
        if (measurementInProgress != null) {
            var hBox = new StopwatchRecordMeasurementInProgressVBox(measurementInProgress);
            children.add(hBox);
        }
        children.add(new Separator());
    }
}
