package time_tracker.component.report.groupedByRecord;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import time_tracker.common.annotation.NonNull;
import time_tracker.model.StopwatchRecord;

import java.util.List;

import static time_tracker.component.Utils.load;

public class RecordVBox extends VBox {

    @FXML
    private Label recordNameLabel;
    @FXML
    private VBox recordDatesWrapperVBox;

    public RecordVBox(
            @NonNull final String recordName,
            @NonNull final List<StopwatchRecord> records) {
        load("/fxml/report/groupedByRecord/RecordVBox.fxml", this);

        recordNameLabel.setText(recordName);

        for (final StopwatchRecord record : records) {
            if (record.getMeasurementsProperty().isEmpty()) {
                continue;
            }
            recordDatesWrapperVBox.getChildren().add(new DateWithMeasurementsVBox(record));
        }
    }
}
