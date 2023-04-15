package time_tracker.component.stopwatch.date;

import io.github.palexdev.materialfx.controls.MFXDatePicker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.config.GlobalContext;
import time_tracker.service.DayDataService;

import static time_tracker.component.Utils.load;

@Log
public class CreateDateVBox extends VBox {

    @FXML
    private Button cancelButton;
    @FXML
    private Button createButton;
    @FXML
    private MFXDatePicker datePicker;

    public CreateDateVBox(@NonNull Stage stage) {
        load("/fxml/stopwatch/date/CreateDateVBox.fxml", this);

        createButton.setOnMouseClicked(e -> {
            log.fine("'Create' button is clicked");
            var dateToCreate = datePicker.getValue();

            log.fine(() -> "Create " + dateToCreate + " date");
            var dayDataService = GlobalContext.get(DayDataService.class);
            dayDataService.create(dateToCreate);
            stage.close();
        });

        cancelButton.setOnMouseClicked(e -> stage.close());
    }
}
