package time_tracker.component.stopwatch;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.NonNull;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.config.GlobalContext;
import time_tracker.config.properties.AppProperties;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;
import time_tracker.service.StopwatchRecordService;

import java.io.IOException;
import java.util.logging.Level;

import static time_tracker.component.Utils.load;

@Log
public class StopwatchRecordVBox extends VBox {

    @FXML
    private Label recordIdLabel;
    @FXML
    private Text nameText;
    @FXML
    private Text totalTimeText;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private VBox finishedMeasurementsVBox;
    @FXML
    private VBox inProgressMeasurementVBox;

    private final StopwatchRecord stopwatchRecord;
    private final StopwatchRecordService stopwatchRecordService;

    public StopwatchRecordVBox(
            @NonNull final StopwatchRecord stopwatchRecord
    ) {
        load("/fxml/stopwatch/StopwatchRecordVBox.fxml", this);

        nameText.setText(stopwatchRecord.getName());

        startButton.disableProperty()
                .bind(stopwatchRecord.getHasMeasurementInProgress());
        stopButton.disableProperty()
                .bind(Bindings.not(stopwatchRecord.getHasMeasurementInProgress()));

        this.stopwatchRecord = stopwatchRecord;
        this.stopwatchRecordService = GlobalContext.get(StopwatchRecordService.class);

        var appProperties = GlobalContext.get(StopwatchProperties.class);
        var isDevMode = appProperties.isDevMode();
        recordIdLabel.setVisible(isDevMode);
        recordIdLabel.setText(Long.toString(stopwatchRecord.getId()));

        bindTotalTime();
        bindMeasurements();
        bindMeasurementInProgress();
    }

    @FXML
    protected void start() {
        log.log(Level.FINE, "stopwatchStartButton is clicked");
        stopwatchRecordService.startNewMeasurement(stopwatchRecord);
    }

    @FXML
    protected void stop() {
        log.log(Level.FINE, "stopwatchStopButton is clicked");
        stopwatchRecordService.stopMeasurement(stopwatchRecord);
    }

    private void bindTotalTime() {
        var measurementsTotalInSecsLongBinding = stopwatchRecord.getMeasurementsTotalInSecsLongBinding();

        StringBinding longBinding = new StringBinding() {
            {
                super.bind(measurementsTotalInSecsLongBinding);
            }

            @Override
            protected String computeValue() {
                var totalInSecs = measurementsTotalInSecsLongBinding.get();
                return Utils.formatDuration(totalInSecs);
            }
        };
        totalTimeText.textProperty()
                .bind(longBinding);
    }

    private void bindMeasurements() {
        ObservableList<Node> records = FXCollections.observableArrayList();
        var stopwatchRecords = stopwatchRecord.getMeasurementsProperty();
        stopwatchRecords.addListener((ListChangeListener<StopwatchRecordMeasurement>) c -> {
            log.fine(() -> "stopwatch records have been changed ");
            log.finest(() -> "actual " + c);
            records.clear();
            c.getList().stream()
                    .map(MeasurementVBox::new)
                    .forEach(records::add);
        });
        stopwatchRecords.stream()
                .map(MeasurementVBox::new)
                .forEach(records::add);
        Bindings.bindContent(finishedMeasurementsVBox.getChildren(), records);
    }

    private void bindMeasurementInProgress() {
        stopwatchRecord.getMeasurementInProgressProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        inProgressMeasurementVBox.getChildren().clear();
                        return;
                    }
                    var hBox = new MeasurementInProgressVBox(newValue);
                    inProgressMeasurementVBox.getChildren().add(hBox);
                });
    }

}
