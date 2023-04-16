package time_tracker.component.stopwatch.date;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.config.GlobalContext;
import time_tracker.model.StopWatchAppState;
import time_tracker.service.DayDataService;

import java.time.Duration;

import static time_tracker.component.Utils.load;

@Log
public class FastEditButtonsVBox extends VBox {

    private final StopWatchAppState stopWatchAppState;

    public FastEditButtonsVBox() {
        load("/fxml/stopwatch/date/FastEditButtonsVBox.fxml", this);

        stopWatchAppState = GlobalContext.get(StopWatchAppState.class);
    }

    @FXML
    protected void setHoliday(){
        var chosenDate = stopWatchAppState.getChosenDateProperty().getValue();
        var dayData = stopWatchAppState.getDateToDayData().get(chosenDate);
        log.fine("'Holiday' button is clicked for dayData = " + dayData.getId());
        dayData.setExpected(Duration.ZERO);
        dayData.setNote("Holiday");
        var dayDataService = GlobalContext.get(DayDataService.class);
        dayDataService.save(dayData);
    }

    @FXML
    protected void setWeekend(){
        var chosenDate = stopWatchAppState.getChosenDateProperty().getValue();
        var dayData = stopWatchAppState.getDateToDayData().get(chosenDate);
        log.fine("'Weekend' button is clicked for dayData = " + dayData.getId());
        dayData.setExpected(Duration.ZERO);
        dayData.setNote("Weekend");
        var dayDataService = GlobalContext.get(DayDataService.class);
        dayDataService.save(dayData);
    }

    @FXML
    protected void setSick(){
        var chosenDate = stopWatchAppState.getChosenDateProperty().getValue();
        var dayData = stopWatchAppState.getDateToDayData().get(chosenDate);
        log.fine("'Sick' button is clicked for dayData = " + dayData.getId());
        dayData.setExpected(Duration.ZERO);
        dayData.setNote("Sick");
        var dayDataService = GlobalContext.get(DayDataService.class);
        dayDataService.save(dayData);
    }
}
