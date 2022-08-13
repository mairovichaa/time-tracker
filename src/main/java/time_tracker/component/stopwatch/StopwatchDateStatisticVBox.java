package time_tracker.component.stopwatch;

import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.config.GlobalContext;
import time_tracker.model.StopWatchAppState;

import static time_tracker.component.Utils.load;

@Log
public class StopwatchDateStatisticVBox extends VBox {

    @FXML
    private Label totalAmountOfTimeLabel;

    @FXML
    private Label amountOfRecordsLabel;


    private final StopWatchAppState stopWatchAppState;


    public StopwatchDateStatisticVBox() {
        load("/fxml/stopwatch/StopwatchDateStatisticVBox.fxml", this);

        stopWatchAppState = GlobalContext.get(StopWatchAppState.class);

        stopWatchAppState.getChosenDateProperty()
                .addListener((observable, oldValue, newValue) -> rebind());
        rebind();
    }

    private void rebind() {
        var chosenDate = stopWatchAppState.getChosenDateProperty().getValue();
        var dayData = stopWatchAppState.getDateToDayData().get(chosenDate);
        var measurementsTotalTimeInSecs = dayData.getTotalInSecs();
        totalAmountOfTimeLabel.textProperty().unbind();
        totalAmountOfTimeLabel.textProperty()
                .bind(new StringBinding() {
                    {
                        super.bind(measurementsTotalTimeInSecs);
                    }

                    @Override
                    protected String computeValue() {
                        return Utils.formatDuration(measurementsTotalTimeInSecs.getValue());
                    }
                });

        amountOfRecordsLabel.textProperty().unbind();
        amountOfRecordsLabel.textProperty()
                .bind(new StringBinding() {
                    {
                        super.bind(dayData.getAmount());
                    }

                    @Override
                    protected String computeValue() {
                        return dayData.getAmount().getValue() + "";
                    }
                });
    }
}
