package time_tracker.component.report;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.component.report.gropedByDate.ListOfDatesVBox;
import time_tracker.component.report.groupedByRecord.ListOfRecordsVBox;
import time_tracker.model.ReportState;

import java.util.logging.Level;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;

@Log
public class ReportVBox extends VBox {

    @FXML
    private ListOfRecordsVBox listOfRecordsVBox;

    @FXML
    private ListOfDatesVBox listOfDatesVBox;

    public ReportVBox() {
        load("/fxml/report/ReportVBox.fxml", this);
        log.log(Level.FINE, "Create ReportVBox");

        ReportState reportState = CONTEXT.get(ReportState.class);
        reportState.getGroupByRecord()
                .addListener((observable, oldValue, newValue) -> showUsingCorrectGrouping(newValue));
        showUsingCorrectGrouping(reportState.getGroupByRecord().getValue());
    }

    private void showUsingCorrectGrouping(final Boolean newValue) {
        if (newValue) {
            listOfRecordsVBox.setManaged(true);
            listOfRecordsVBox.setVisible(true);
            listOfDatesVBox.setManaged(false);
            listOfDatesVBox.setVisible(false);
        } else {
            listOfRecordsVBox.setManaged(false);
            listOfRecordsVBox.setVisible(false);
            listOfDatesVBox.setManaged(true);
            listOfDatesVBox.setVisible(true);
        }
    }
}
