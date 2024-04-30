package time_tracker.component.report.groupedByRecord;

import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import time_tracker.common.annotation.NonNull;
import time_tracker.model.ReportState;
import time_tracker.model.StopwatchRecord;

import java.util.List;
import java.util.Map;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;

public class ListOfRecordsVBox extends VBox {

    @FXML
    private VBox recordsVBox;

    public ListOfRecordsVBox() {
        load("/fxml/report/groupedByRecord/ListOfRecordsVBox.fxml", this);
        var reportState = CONTEXT.get(ReportState.class);
        ObjectProperty<Map<String, List<StopwatchRecord>>> recordNameToRecordsProperty = reportState.getRecordNameToRecordsProperty();
        recordNameToRecordsProperty.addListener((observable, oldValue, newValue) -> refreshRecords(newValue));
        refreshRecords(recordNameToRecordsProperty.getValue());
    }

    private void refreshRecords(@NonNull final Map<String, List<StopwatchRecord>> recordNameToRecords) {
        recordsVBox.getChildren().clear();
        recordNameToRecords.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(it -> new RecordVBox(it.getKey(), it.getValue()))
                .forEach(recordsVBox.getChildren()::add);
    }
}
