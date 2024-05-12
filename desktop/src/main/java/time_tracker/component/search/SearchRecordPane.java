package time_tracker.component.search;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.LongBinding;
import javafx.beans.binding.StringBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.TimeTrackerApp;
import time_tracker.Utils;
import time_tracker.common.annotation.NonNull;
import time_tracker.component.common.Icon;
import time_tracker.component.common.IconUtils;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchRecordMeasurement;
import time_tracker.controller.AppStateController;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;

import static javafx.beans.binding.Bindings.createStringBinding;
import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;
import static time_tracker.component.common.Confirmation.requireConfirmation;
import static time_tracker.component.common.IconUtils.initIconLabeled;

@Log
public class SearchRecordPane extends Pane {

    @FXML
    private Label recordIdLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label totalTimeLabel;
    @FXML
    private Label totalTimeIconLabel;
    @FXML
    private Button goToDateIconLabel;
    @FXML
    private VBox measurementsWrapperVBox;
    @FXML
    private Button trackButton;
    @FXML
    private Button notTrackButton;
    @FXML
    private Button deleteButton;

    private final AppStateController appStateController;
    private final StopwatchRecord record;
    // reference has to be stored as WeakListChangeListener is used to register it
    private final ListChangeListener<StopwatchRecordMeasurement> measurementsChangesListener;
    private final StopWatchAppState stopWatchAppState;

    public SearchRecordPane(@NonNull final StopwatchRecord record) {
        load("/fxml/search/SearchRecordPane.fxml", this);
        this.record = record;

        LocalDate date = record.getDate();
        String formattedLocalDate = Utils.formatLocalDate(date);
        // TODO make it a property
        dateLabel.textProperty().set(formattedLocalDate);

        this.appStateController = CONTEXT.get(AppStateController.class);
        stopWatchAppState = CONTEXT.get(StopWatchAppState.class);

        StopwatchProperties appProperties = CONTEXT.get(StopwatchProperties.class);
        var isDevMode = appProperties.isDevMode();
        recordIdLabel.setVisible(isDevMode);
        recordIdLabel.setManaged(isDevMode);
        recordIdLabel.setText("#" + record.getId());

        bindTotalTime();
        measurementsChangesListener = bindAmountOfMeasurements();

        initIconLabeled(trackButton, 20, Icon.CHECK);
        trackButton.visibleProperty().bind(Bindings.not(this.record.getTrackedProperty()));
        trackButton.managedProperty().bind(Bindings.not(this.record.getTrackedProperty()));

        initIconLabeled(goToDateIconLabel, 12, Icon.INPUT);

        IconUtils.initIconLabeled(notTrackButton, 20, Icon.CHECK, List.of("icon-button", "icon-button-green"), List.of("button-icon-green"));
        notTrackButton.visibleProperty().bind(this.record.getTrackedProperty());
        notTrackButton.managedProperty().bind(this.record.getTrackedProperty());

        IconUtils.initIconLabeled(totalTimeIconLabel, 20, Icon.STOPWATCH, List.of("icon-label-black"), List.of("label-icon-black"));

        initIconLabeled(deleteButton, 15, Icon.DELETE);
    }

    @NonNull
    private ListChangeListener<StopwatchRecordMeasurement> bindAmountOfMeasurements() {
        ObservableList<StopwatchRecordMeasurement> stopwatchRecords = record.getMeasurementsProperty();
        var measurementsChangesListener = (ListChangeListener<StopwatchRecordMeasurement>) c -> {
            log.fine(() -> "stopwatch records have been changed. Actual " + c);
            ObservableList<? extends StopwatchRecordMeasurement> measurements = c.getList();
            setMeasurementsReport(measurements);
        };

        stopwatchRecords.addListener(new WeakListChangeListener<>(measurementsChangesListener));
        setMeasurementsReport(stopwatchRecords);
        return measurementsChangesListener;
    }

    private void setMeasurementsReport(@NonNull final List<? extends StopwatchRecordMeasurement> measurements) {
        List<SearchMeasurementVBox> measurementVBoxes = measurements.stream()
                .map(SearchMeasurementVBox::new)
                .toList();

        measurementsWrapperVBox.getChildren().clear();
        measurementsWrapperVBox.getChildren().addAll(measurementVBoxes);
    }

    @FXML
    protected void delete(@NonNull final MouseEvent event) {
        log.log(Level.FINE, "deleteButton is clicked");

        requireConfirmation().whenComplete((it, ex) -> {
            if (it) {
                appStateController.delete(record);
            } else {
                log.fine(() -> "'No' button is clicked");
            }
        });
        event.consume();
    }

    private void bindTotalTime() {
        LongBinding measurementsTotalInSecsLongBinding = record.getMeasurementsTotalInSecsLongBinding();

        StringBinding longBinding = createStringBinding(
                () -> {
                    long totalInSecs = measurementsTotalInSecsLongBinding.get();
                    return Utils.formatDuration(totalInSecs);
                },
                measurementsTotalInSecsLongBinding
        );

        totalTimeLabel.textProperty()
                .bind(longBinding);
    }

    @FXML
    protected void track() {
        log.log(Level.FINE, "'Track' button is clicked");
        record.setTracked(true);
        appStateController.store(record);
    }

    @FXML
    protected void notTrack() {
        log.log(Level.FINE, "'Not track' button is clicked");
        record.setTracked(false);
        appStateController.store(record);
    }

    @FXML
    protected void goToDate() {
        log.log(Level.FINE, "'Go to date' button is clicked");
        LocalDate date = record.getDate();

        log.log(Level.FINE, () ->
                String.format("Date = '%s', record name = '%s', record id = '%s'", date, record.getName(), record.getId()));

        stopWatchAppState.setChosenDate(date);
        stopWatchAppState.getChosenStopwatchRecord().set(record);
        TimeTrackerApp.showStopwatch();
    }

}
