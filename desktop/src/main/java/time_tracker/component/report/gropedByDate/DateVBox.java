package time_tracker.component.report.gropedByDate;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import time_tracker.Utils;
import time_tracker.common.annotation.NonNull;
import time_tracker.model.StopwatchRecord;

import java.util.Comparator;
import java.util.List;

import static time_tracker.component.Utils.load;

public class DateVBox extends VBox {

    @FXML
    private Label dateLabel;

    @FXML
    private VBox dateRecordsWrapperVBox;

    public DateVBox(@NonNull final List<StopwatchRecord> records) {
        load("/fxml/report/groupedByDate/DateVBox.fxml", this);

        StopwatchRecord record = records.get(0);
        String formattedLocalDate = Utils.formatLocalDate(record.getDate());
        dateLabel.setText(formattedLocalDate);

        records.stream()
                .sorted(Comparator.comparing(StopwatchRecord::getName))
                .map(RecordWithMeasurementsVBox::new)
                .forEach(dateRecordsWrapperVBox.getChildren()::add);
    }

}
