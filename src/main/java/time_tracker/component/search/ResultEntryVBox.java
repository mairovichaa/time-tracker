package time_tracker.component.search;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import time_tracker.Utils;
import time_tracker.annotation.NonNull;
import time_tracker.component.stopwatch.MeasurementVBox;
import time_tracker.model.StopwatchRecord;

import java.io.IOException;

import static time_tracker.component.Utils.*;


public class ResultEntryVBox extends VBox {

    @FXML
    private Text name;

    @FXML
    private Text date;

    @FXML
    private VBox measurements;

    public ResultEntryVBox(@NonNull final StopwatchRecord record) {
        load("/fxml/search/ResultEntryVBox.fxml", this);

        var recordName = record.getName();
        name.setText(recordName);
        var formattedDate = Utils.formatLocalDate(record.getDate());
        date.setText(formattedDate);

        var children = measurements.getChildren();

        if (!record.getMeasurementsProperty().isEmpty()) {
            record.getMeasurementsProperty()
                    .stream()
                    .map(MeasurementVBox::new)
                    .forEach(children::add);
        }
    }
}
