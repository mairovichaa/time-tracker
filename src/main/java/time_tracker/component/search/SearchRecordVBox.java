package time_tracker.component.search;

import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.beans.binding.LongBinding;
import javafx.beans.binding.StringBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.annotation.NonNull;
import time_tracker.config.GlobalContext;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;
import time_tracker.service.AppStateService;
import time_tracker.service.StopwatchRecordService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static javafx.beans.binding.Bindings.createStringBinding;
import static time_tracker.Constants.DATA_TIME_FORMATTER;
import static time_tracker.component.Utils.load;

@Log
public class SearchRecordVBox extends VBox {

    @FXML
    private Label recordIdLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label totalTimeLabel;
    @FXML
    private Label deleteLabel;
    @FXML
    private Label amountOfMeasurements;
    @FXML
    private MFXToggleButton trackedToggle;
    @FXML
    private VBox measurementsWrapperVBox;

    private final AppStateService appStateService;
    private final StopwatchRecord record;
    // reference has to be stored as WeakListChangeListener is used to register it
    private final ListChangeListener<StopwatchRecordMeasurement> measurementsChangesListener;

    public SearchRecordVBox(@NonNull final StopwatchRecord record) {
        load("/fxml/search/SearchRecordVBox.fxml", this);
        this.record = record;

        LocalDate date = record.getDate();
        String formattedLocalDate = Utils.formatLocalDate(date);
        // TODO make it a property
        dateLabel.textProperty().set(formattedLocalDate);

        trackedToggle.selectedProperty()
                .bindBidirectional(record.getTrackedProperty());

        this.appStateService = GlobalContext.get(AppStateService.class);

        // TODO it seems that listener has to be removed or to be moved to another place
        record.getTrackedProperty()
                .addListener((observable, oldValue, newValue) -> appStateService.store());

        StopwatchProperties appProperties = GlobalContext.get(StopwatchProperties.class);
        var isDevMode = appProperties.isDevMode();
        recordIdLabel.setVisible(isDevMode);
        recordIdLabel.setManaged(isDevMode);
        recordIdLabel.setText(Long.toString(record.getId()));

        bindTotalTime();
        measurementsChangesListener = bindAmountOfMeasurements();
    }

    @NonNull
    private ListChangeListener<StopwatchRecordMeasurement> bindAmountOfMeasurements() {
        ObservableList<StopwatchRecordMeasurement> stopwatchRecords = record.getMeasurementsProperty();
        var measurementsChangesListener = (ListChangeListener<StopwatchRecordMeasurement>) c -> {
            log.fine(() -> "stopwatch records have been changed. Actual " + c);
            ObservableList<? extends StopwatchRecordMeasurement> measurements = c.getList();
            setAmountOfMeasurements(measurements);
            setMeasurementsReport(measurements);
        };

        stopwatchRecords.addListener(new WeakListChangeListener<>(measurementsChangesListener));
        setAmountOfMeasurements(stopwatchRecords);
        setMeasurementsReport(stopwatchRecords);
        return measurementsChangesListener;
    }

    private void setAmountOfMeasurements(@NonNull final List<? extends StopwatchRecordMeasurement> measurements) {
        int amountOfMeasurements = measurements.size();
        this.amountOfMeasurements.setText(Integer.toString(amountOfMeasurements));
    }

    private void setMeasurementsReport(@NonNull final List<? extends StopwatchRecordMeasurement> measurements) {
        List<SearchMeasurementVBox> measurementVBoxes = measurements.stream()
                .map(SearchMeasurementVBox::new)
                .collect(Collectors.toList());

        measurementsWrapperVBox.getChildren().clear();
        measurementsWrapperVBox.getChildren().addAll(measurementVBoxes);
    }

    @FXML
    protected void delete(@NonNull final MouseEvent event) {
        log.log(Level.FINE, "deleteButton is clicked");

        appStateService.delete(record);
        event.consume();
    }

    private void bindTotalTime() {
        LongBinding measurementsTotalInSecsLongBinding = record.getMeasurementsTotalInSecsLongBinding();

        StringBinding longBinding =  createStringBinding(
                () -> {
                    long totalInSecs = measurementsTotalInSecsLongBinding.get();
                    return Utils.formatDuration(totalInSecs);
                },
                measurementsTotalInSecsLongBinding
        );

        totalTimeLabel.textProperty()
                .bind(longBinding);
    }
}
