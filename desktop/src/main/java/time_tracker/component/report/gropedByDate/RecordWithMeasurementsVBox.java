package time_tracker.component.report.gropedByDate;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import time_tracker.common.annotation.NonNull;
import time_tracker.component.report.common.MeasurementRecordVBox;
import time_tracker.model.StopwatchRecord;

import static time_tracker.component.Utils.load;

public class RecordWithMeasurementsVBox extends VBox {

    @FXML
    private Label nameLabel;

    @FXML
    private VBox measurementsWrapperVBox;

    public RecordWithMeasurementsVBox(@NonNull final StopwatchRecord record) {
        load("/fxml/report/groupedByDate/RecordWithMeasurementsVBox.fxml", this);

        String recordName = record.getName();
        nameLabel.setText(recordName);

        record.getMeasurementsProperty()
                .stream()
                .map(MeasurementRecordVBox::new)
                .forEach(measurementsWrapperVBox.getChildren()::add);
    }
}
