package time_tracker.component.stopwatch;

import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.beans.binding.StringBinding;
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
import time_tracker.service.DayDataService;

import java.time.Duration;

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
    private Label trackedTimeLabel;
    @FXML
    private MFXToggleButton trackedToggle;
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
        var measurementsTotalTimeInSecs = dayData.getTotalInSecsProperty();
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

        var trackedInSecs = dayData.getTrackedInSecsProperty();
        trackedTimeLabel.textProperty().unbind();
        trackedTimeLabel.textProperty()
                .bind(new StringBinding() {
                          {
                              super.bind(trackedInSecs);
                          }

                          @Override
                          protected String computeValue() {
                              return Utils.formatDuration(trackedInSecs.getValue());
                          }
                      }
                );

        trackedToggle.selectedProperty()
                .unbind();
        trackedToggle.selectedProperty()
                .bind(dayData.getTracked());

        amountOfRecordsLabel.textProperty().unbind();
        amountOfRecordsLabel.textProperty()
                .bind(new StringBinding() {
                    {
                        super.bind(dayData.getAmountOfRecordsProperty());
                    }

                    @Override
                    protected String computeValue() {
                        return dayData.getAmountOfRecords() + "";
                    }
                });

        expectedAmountOfTime.textProperty().unbind();
        var expectedTotalInSecsProperty = dayData.getExpectedTotalInSecsProperty();
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
                        super.bind(dayData.getTimeToWorkLeftProperty());
                    }

                    @Override
                    protected String computeValue() {
                        return Utils.formatDuration(dayData.getTimeToWorkLeft());
                    }
                });

        overtime.textProperty().unbind();
        overtime.textProperty()
                .bind(new StringBinding() {
                    {
                        super.bind(dayData.getOvertimeProperty());
                    }

                    @Override
                    protected String computeValue() {
                        return Utils.formatDuration(dayData.getOvertime());
                    }
                });

        comment.textProperty()
                .bind(new StringBinding() {
                    {
                        bind(dayData.getNoteProperty());
                    }

                    @Override
                    protected String computeValue() {
                        var comment = dayData.getNote();
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
            Scene dialogScene = new Scene(dialogVbox);
            dialog.setScene(dialogScene);
            dialog.show();
        });
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
