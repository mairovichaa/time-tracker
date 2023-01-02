package time_tracker.component.stopwatch;

import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.extern.java.Log;
import time_tracker.TimeTrackerApp;
import time_tracker.Utils;
import time_tracker.config.GlobalContext;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopwatchRecordMeasurement;
import time_tracker.service.StopwatchMeasurementService;

import static time_tracker.Constants.DATA_TIME_FORMATTER;
import static time_tracker.component.Utils.load;

@Log
public class MeasurementVBox extends VBox {

    @FXML
    private Label measurementIdLabel;
    @FXML
    private Label startedAt;
    @FXML
    private Label finishedAt;
    @FXML
    private Label total;
    @FXML
    private Label comment;

    @FXML
    private Button edit;
    @FXML
    private Button deleteButton;

    public MeasurementVBox(@NonNull final StopwatchRecordMeasurement measurement) {
        load("/fxml/stopwatch/MeasurementVBox.fxml", this);

        var appProperties = GlobalContext.get(StopwatchProperties.class);
        var isDevMode = appProperties.isDevMode();
        measurementIdLabel.setVisible(isDevMode);
        measurementIdLabel.setText(Long.toString(measurement.getId()));


        startedAt.textProperty()
                .bind(new StringBinding() {
                    {
                        super.bind(measurement.getStartedAtProperty());
                    }

                    @Override
                    protected String computeValue() {
                        var startedAt = measurement.getStartedAtProperty().getValue();
                        return DATA_TIME_FORMATTER.format(startedAt);
                    }
                });

        finishedAt.textProperty()
                .bind(new StringBinding() {
                    {
                        super.bind(measurement.getStoppedAtProperty());
                    }

                    @Override
                    protected String computeValue() {
                        var stoppedAt = measurement.getStoppedAtProperty().getValue();
                        return DATA_TIME_FORMATTER.format(stoppedAt);
                    }
                });
        total.textProperty()
                .bind(new StringBinding() {
                    {
                        super.bind(measurement.getDurationProperty());
                    }

                    @Override
                    protected String computeValue() {
                        var duration = measurement.getDuration();
                        return Utils.formatDuration(duration);
                    }
                });


        comment.textProperty()
                .bind(new StringBinding() {
                    {
                        bind(measurement.getNoteProperty());
                    }

                    @Override
                    protected String computeValue() {
                        var comment = measurement.getNoteProperty().getValue();
                        if (comment.isEmpty()) {
                            return "No comment";
                        }
                        return comment;
                    }
                });

        edit.setOnMouseClicked(e -> {
            log.fine("Edit button is clicked for measurement = " + measurement.getId());
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(TimeTrackerApp.primaryStage);
            VBox dialogVbox = new MeasurementEditVBox(measurement, dialog);
            Scene dialogScene = new Scene(dialogVbox, 600, 600);
            dialog.setScene(dialogScene);
            dialog.show();
        });

        var stopwatchMeasurementService = GlobalContext.get(StopwatchMeasurementService.class);
        deleteButton.setOnMouseClicked(e -> {
            log.fine("Delete button is clicked for measurement = " + measurement.getId());
            stopwatchMeasurementService.delete(measurement.getId());
        });

    }
}