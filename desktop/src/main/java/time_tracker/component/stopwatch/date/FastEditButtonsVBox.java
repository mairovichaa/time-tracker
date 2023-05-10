package time_tracker.component.stopwatch.date;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.enums.ButtonType;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.common.GlobalContext;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopWatchAppState;
import time_tracker.service.DayDataService;

import java.time.Duration;

import static time_tracker.component.Utils.load;

@Log
public class FastEditButtonsVBox extends VBox {

    @FXML
    private FlowPane fastEditButtonsContainer;

    private final StopWatchAppState stopWatchAppState;

    public FastEditButtonsVBox() {
        load("/fxml/stopwatch/date/FastEditButtonsVBox.fxml", this);

        stopWatchAppState = GlobalContext.get(StopWatchAppState.class);
        var stopwatchProperties = GlobalContext.get(StopwatchProperties.class);
        stopwatchProperties.getDates()
                .getFastEditButtons()
                .stream()
                .map(fastEditButtonProperties -> {
                    var fastEditButtonName = fastEditButtonProperties.getName();
                    var button = new MFXButton(fastEditButtonName);
                    button.setButtonType(ButtonType.RAISED);
                    button.setOnMouseClicked(e -> {
                        var chosenDate = stopWatchAppState.getChosenDateProperty().getValue();
                        var dayData = stopWatchAppState.getDateToDayData().get(chosenDate);
                        log.fine("'Holiday' button is clicked for dayData = " + dayData.getId());

                        var expectedDuration = Duration.parse("PT" + fastEditButtonProperties.getExpected());
                        dayData.setExpected(expectedDuration);
                        dayData.setNote(fastEditButtonName);
                        var dayDataService = GlobalContext.get(DayDataService.class);
                        dayDataService.save(dayData);
                    });
                    return button;
                }).forEach(it -> fastEditButtonsContainer.getChildren().add(it));
    }
}
