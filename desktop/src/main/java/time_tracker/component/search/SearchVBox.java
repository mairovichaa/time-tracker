package time_tracker.component.search;

import javafx.scene.layout.VBox;
import lombok.extern.java.Log;

import java.util.logging.Level;

import static time_tracker.component.Utils.load;

@Log
public class SearchVBox extends VBox {

    public SearchVBox() {
        load("/fxml/search/SearchVBox.fxml", this);
        log.log(Level.FINE, "Create SearchVBox");
    }
}
