package time_tracker.component.report;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import lombok.extern.java.Log;
import time_tracker.component.stopwatch.ListOfDatesVbox;

import java.util.logging.Level;

@Log
public class ReportTab extends Tab {
    public ReportTab() {
        super("Report");
        log.log(Level.FINE, "Create ReportTab");

        var hBoxWrapper = new HBox();
        hBoxWrapper.setSpacing(10);

        var listOfDatesVbox = new ListOfDatesVbox();
        var reportVBox = new ReportVBox();

        hBoxWrapper.getChildren()
                .addAll(
                        listOfDatesVbox,
                        reportVBox
                );
        hBoxWrapper.setPadding(new Insets(10, 0, 0, 10));
        this.setContent(hBoxWrapper);

    }
}
