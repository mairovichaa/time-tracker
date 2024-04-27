package time_tracker.component.report.groupedByRecord;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import time_tracker.Utils;
import time_tracker.common.annotation.NonNull;
import time_tracker.component.report.common.MeasurementRecordVBox;
import time_tracker.model.StopwatchRecord;

import static time_tracker.component.Utils.load;

public class DateWithMeasurementsVBox extends VBox {

    @FXML
    private Label dateLabel;
    @FXML
    private VBox measurementsWrapperVBox;

    public DateWithMeasurementsVBox(@NonNull final StopwatchRecord record) {
        load("/fxml/report/groupedByRecord/DateWithMeasurementsVBox.fxml", this);

        String date = Utils.formatLocalDate(record.getDate());
        dateLabel.setText(date);

        record.getMeasurementsProperty()
                .stream()
                .map(MeasurementRecordVBox::new)
                .forEach(measurementsWrapperVBox.getChildren()::add);
    }
}
