package time_tracker.component.stopwatch;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.LongProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import time_tracker.TimeTrackerApp;
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

    @FXML
    private Label expectedAmountOfTime;
    @FXML
    private Label timeToWorkLeft;
    @FXML
    private Label overtime;
    @FXML
    private Label comment;
    @FXML
    private Button editButton;

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

        expectedAmountOfTime.textProperty().unbind();
        var expectedTotalInSecsProperty = dayData.getExpectedTotalInSecs();
        expectedAmountOfTime.textProperty()
                .bind(new StringBinding() {
                    {
                        super.bind(expectedTotalInSecsProperty);
                    }
                    @Override
                    protected String computeValue() {
                        return Utils.formatDuration(expectedTotalInSecsProperty.getValue());
                    }
                });

        timeToWorkLeft.textProperty().unbind();
        timeToWorkLeft.textProperty()
                .bind(new StringBinding() {
                    {
                        super.bind(measurementsTotalTimeInSecs, expectedTotalInSecsProperty);
                    }
                    @Override
                    protected String computeValue() {
                        var diffInSecs = expectedTotalInSecsProperty.getValue() - measurementsTotalTimeInSecs.getValue();
                        var absDiff = Math.max(diffInSecs, 0);
                        return Utils.formatDuration(absDiff);
                    }
                });

        overtime.textProperty().unbind();
        overtime.textProperty()
                .bind(new StringBinding() {
                    {
                        super.bind(measurementsTotalTimeInSecs, expectedTotalInSecsProperty);
                    }
                    @Override
                    protected String computeValue() {
                        var diffInSecs = expectedTotalInSecsProperty.getValue() - measurementsTotalTimeInSecs.getValue();
                        var absDiff = Math.abs(Math.min(diffInSecs, 0));
                        return Utils.formatDuration(absDiff);
                    }
                });

        comment.textProperty()
                .bind(new StringBinding() {
                    {
                        bind(dayData.getNoteProperty());
                    }

                    @Override
                    protected String computeValue() {
                        var comment = dayData.getNoteProperty().getValue();
                        if (comment == null) {
                            return "No comment";
                        }
                        return comment;
                    }
                });

        editButton.setOnMouseClicked(e -> {
            log.fine("Edit button is clicked for dayData = " + dayData.getId());
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(TimeTrackerApp.primaryStage);
            VBox dialogVbox = new DayDataEditVBox(dayData, dialog);
            Scene dialogScene = new Scene(dialogVbox, 600, 600);
            dialog.setScene(dialogScene);
            dialog.show();
        });
    }
}
