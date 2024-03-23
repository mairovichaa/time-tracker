package time_tracker.component.stopwatch;

import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.common.GlobalContext;
import time_tracker.component.common.DialogFactory;
import time_tracker.component.common.Icon;
import time_tracker.model.StopWatchAppState;

import java.util.List;

import static time_tracker.component.Utils.load;
import static time_tracker.component.common.IconButton.initIconButton;

@Log
public class StopwatchDateStatisticPane extends Pane {

    @FXML
    private Label totalAmountOfTimeLabel;
    @FXML
    private Label expectedAmountOfTime;
    @FXML
    private Label timeToWorkLeft;
    @FXML
    private Label overtimeIconLabel;
    @FXML
    private Label expectedIconLabel;
    @FXML
    private Label timeToWorkLeftIconLabel;
    @FXML
    private Label totalAmountOfTimeIconLabel;
    @FXML
    private Label overtime;
    @FXML
    private Label comment;
    @FXML
    private Button editButton;
    @FXML
    private Label trackedLabel;
    @FXML
    private Label nonTrackedLabel;

    private final StopWatchAppState stopWatchAppState;


    public StopwatchDateStatisticPane() {
        load("/fxml/stopwatch/StopwatchDateStatisticPane.fxml", this);

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

        initIconButton(overtimeIconLabel, 16, Icon.WORKSPACE_PREMIUM, List.of("icon-label-black"), List.of("label-icon-black"));
        initIconButton(totalAmountOfTimeIconLabel, 16, Icon.TASK_ALT, List.of("icon-label-black"), List.of("label-icon-black"));
        initIconButton(timeToWorkLeftIconLabel, 16, Icon.CONSTRUCTION, List.of("icon-label-black"), List.of("label-icon-black"));
        initIconButton(expectedIconLabel, 16, Icon.SCHEDULE, List.of("icon-label-black"), List.of("label-icon-black"));

        initIconButton(nonTrackedLabel, 20, Icon.CHECK, List.of("icon-label-grey"), List.of("label-icon-grey"));
        initIconButton(trackedLabel, 20, Icon.CHECK, List.of("icon-label-green"), List.of("label-icon-green"));
        dayData.getTracked()
                .addListener((observable, oldValue, newValue) -> refreshTracked(newValue));
        refreshTracked(dayData.isTracked());

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

        initIconButton(editButton, 15, Icon.PEN);

        editButton.setOnMouseClicked(e -> {
            log.fine("Edit button is clicked for dayData = " + dayData.getId());
            DialogFactory.createAndShow(
                    stage -> new DayDataEditVBox(dayData, stage),
                    "Edit day info"
            );
        });
    }

    private void refreshTracked(final boolean tracked) {
        if (tracked) {
            nonTrackedLabel.setVisible(false);
            nonTrackedLabel.setManaged(false);
            trackedLabel.setVisible(true);
            trackedLabel.setManaged(true);
        } else {
            nonTrackedLabel.setVisible(true);
            nonTrackedLabel.setManaged(true);
            trackedLabel.setVisible(false);
            trackedLabel.setManaged(false);
        }
    }
}
