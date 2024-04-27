package time_tracker.component.report.gropedByDate;

import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import time_tracker.common.GlobalContext;
import time_tracker.common.annotation.NonNull;
import time_tracker.model.ReportState;
import time_tracker.model.StopwatchRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static time_tracker.component.Utils.load;

public class ListOfDatesVBox extends VBox {

    @FXML
    private VBox recordsVBox;

    public ListOfDatesVBox() {
        load("/fxml/report/groupedByDate/ListOfDatesVBox.fxml", this);

        ReportState reportState = GlobalContext.get(ReportState.class);

        ObjectProperty<Map<LocalDate, List<StopwatchRecord>>> dateToRecordsProperty = reportState.getDateToRecordsProperty();
        dateToRecordsProperty.addListener((observable, oldValue, newValue) -> refreshRecords(newValue));
        refreshRecords(dateToRecordsProperty.getValue());
    }

    private void refreshRecords(@NonNull final Map<LocalDate, List<StopwatchRecord>> dateToRecords) {
        recordsVBox.getChildren().clear();
        dateToRecords.entrySet()
                .stream()
                .sorted(Map.Entry.<LocalDate, List<StopwatchRecord>>comparingByKey().reversed())
                .map(it -> new DateVBox(it.getValue()))
                .forEach(recordsVBox.getChildren()::add);
    }
}
