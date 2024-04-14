package time_tracker.component.report;

import javafx.scene.layout.VBox;
import lombok.extern.java.Log;

import java.util.logging.Level;

import static time_tracker.component.Utils.load;

@Log
public class ReportVBox extends VBox {

    public ReportVBox() {
        load("/fxml/report/ReportVBox.fxml", this);
        log.log(Level.FINE, "Create ReportVBox");
    }
}
