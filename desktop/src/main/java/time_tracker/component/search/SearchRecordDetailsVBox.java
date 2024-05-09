package time_tracker.component.search;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.common.annotation.NonNull;
import time_tracker.component.common.Icon;
import time_tracker.controller.search.StopwatchRecordSearchController;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchSearchState;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;

import static java.util.Comparator.comparing;
import static time_tracker.Constants.AMOUNT_OF_RECORDS_TO_SHOW_IN_A_BATCH;
import static time_tracker.Constants.THRESHOLD_TO_LOAD_NEXT_BATCH;
import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;
import static time_tracker.component.common.IconButton.initIconButton;

@Log
public class SearchRecordDetailsVBox extends VBox {
    private static final String LOG_PATTERN = "Chosen record name is changed from %s to %s";

    @NonNull
    @FXML
    private VBox searchRecordsDetailsWrapperVBox;

    @FXML
    private Label startedAtLabel;

    @FXML
    private Label finishedAtLabel;

    @FXML
    private Label fromStartedAtToFinishedAtIconLabel;

    @NonNull
    @FXML
    private VBox noRecordIsChosenInfoVBox;

    @NonNull
    @FXML
    private Text recordNameLabel;

    @NonNull
    @FXML
    private Label totalTimeLabel;

    @NonNull
    @FXML
    private Label numberOfDatesLabel;

    @NonNull
    @FXML
    private VBox recordsWrapperVBox;

    @FXML
    private Button trackButton;

    @FXML
    private Button notTrackButton;

    @FXML
    private Label totalTimeIconLabel;

    @FXML
    private Label totalTrackedTimeLabel;

    @FXML
    private Label totalNotTrackedTimeLabel;

    @FXML
    private MFXScrollPane scrollPane;

    private final StopwatchSearchState searchState;
    private List<? extends StopwatchRecord> recordsForName = Collections.emptyList();
    private int amountOfShownRecords = 0;

    public SearchRecordDetailsVBox() {
        load("/fxml/search/SearchRecordDetailsVBox.fxml", this);
        scrollPane.vvalueProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue.doubleValue() > THRESHOLD_TO_LOAD_NEXT_BATCH) {
                        showBatchOfRecords();
                    }
                });

        var appState = CONTEXT.get(StopWatchAppState.class);
        var stopwatchRecordSearchController = CONTEXT.get(StopwatchRecordSearchController.class);
        searchState = appState.getSearchState();
        searchState.getChosenRecordName()
                .addListener((observable, oldValue, newRecordName) -> {
                    log.fine(() -> String.format(LOG_PATTERN, oldValue, newRecordName));
                    if (newRecordName != null) {
                        recordNameLabel.setText(newRecordName);

                        var recordsForName = stopwatchRecordSearchController.recordsByName(newRecordName);
                        recordsForName.sort(comparing(StopwatchRecord::getDate).reversed());
                        noRecordIsChosenInfoVBox.setVisible(false);
                        noRecordIsChosenInfoVBox.setManaged(false);
                        searchRecordsDetailsWrapperVBox.setVisible(true);
                        searchRecordsDetailsWrapperVBox.setManaged(true);
                    } else {
                        noRecordIsChosenInfoVBox.setManaged(true);
                        noRecordIsChosenInfoVBox.setVisible(true);
                        searchRecordsDetailsWrapperVBox.setManaged(false);
                        searchRecordsDetailsWrapperVBox.setVisible(false);
                    }
                });
        searchState.getRecordsForChosenName()
                .addListener((ListChangeListener<StopwatchRecord>) c -> refreshRecords(c.getList()));
        searchRecordsDetailsWrapperVBox.setDisable(false);
        searchRecordsDetailsWrapperVBox.setVisible(false);

        initIconButton(fromStartedAtToFinishedAtIconLabel, 20, Icon.LINE_START, List.of("icon-label-black"), List.of("label-icon-black"));
        initIconButton(trackButton, 20, Icon.CHECK);
        initIconButton(notTrackButton, 20, Icon.CHECK, List.of("icon-button", "icon-button-green"), List.of("button-icon-green"));
        initIconButton(totalTimeIconLabel, 20, Icon.STOPWATCH, List.of("icon-label-black"), List.of("label-icon-black"));

    }

    private void refreshRecords(@NonNull List<? extends StopwatchRecord> recordsForName) {
        // TODO create proper binding
        long totalTimeInSecs = recordsForName.stream()
                .mapToLong(StopwatchRecord::getMeasurementsTotalInSecs)
                .sum();
        String formattedTotalTime = Utils.formatDuration(totalTimeInSecs);
        totalTimeLabel.setText(formattedTotalTime);

        List<LongProperty> recordsChangesProperties = recordsForName.stream()
                .map(StopwatchRecord::getIsChangedProperty)
                .toList();

        totalTrackedTimeLabel.textProperty()
                .bind(new StringBinding() {
                    {
                        bind(recordsChangesProperties.toArray(new Observable[]{}));
                    }

                    @Override
                    protected String computeValue() {
                        long totalTrackedTimeInSecs = recordsForName.stream()
                                .filter(StopwatchRecord::isTracked)
                                .mapToLong(StopwatchRecord::getMeasurementsTotalInSecs)
                                .sum();
                        return Utils.formatDuration(totalTrackedTimeInSecs);
                    }
                });

        totalNotTrackedTimeLabel.textProperty()
                .bind(new StringBinding() {
                    {
                        bind(recordsChangesProperties.toArray(new Observable[]{}));
                    }

                    @Override
                    protected String computeValue() {
                        long totalNotTrackedTimeInSecs = recordsForName.stream()
                                .filter(Predicate.not(StopwatchRecord::isTracked))
                                .mapToLong(StopwatchRecord::getMeasurementsTotalInSecs)
                                .sum();
                        return Utils.formatDuration(totalNotTrackedTimeInSecs);
                    }
                });

        int amountOfRecords = recordsForName.size();
        numberOfDatesLabel.setText(Integer.toString(amountOfRecords));

        recordsWrapperVBox.getChildren().clear();
        if (recordsForName.isEmpty()) {
            return;
        }
        this.recordsForName = recordsForName;
        this.amountOfShownRecords = 0;

        showBatchOfRecords();
        List<BooleanProperty> recordsTrackedProperties = recordsForName.stream()
                .map(StopwatchRecord::getTrackedProperty)
                .toList();

        var allRecordsAreTrackedBinding = new BooleanBinding() {
            {
                bind(recordsTrackedProperties.toArray(new Observable[]{}));
            }

            @Override
            protected boolean computeValue() {
                return recordsTrackedProperties.stream()
                        .allMatch(ObservableBooleanValue::get);
            }
        };
        BooleanBinding notAllRecordsAreTrackedBinding = Bindings.not(allRecordsAreTrackedBinding);

        trackButton.visibleProperty().bind(notAllRecordsAreTrackedBinding);
        trackButton.managedProperty().bind(Bindings.not(allRecordsAreTrackedBinding));

        notTrackButton.visibleProperty().bind(allRecordsAreTrackedBinding);
        notTrackButton.managedProperty().bind(allRecordsAreTrackedBinding);


        List<ObjectProperty<LocalDate>> dateProperties = recordsForName.stream()
                .map(StopwatchRecord::getDateProperty)
                .toList();
        startedAtLabel.textProperty()
                .bind(
                        new StringBinding() {

                            {
                                bind(dateProperties.toArray(new Observable[]{}));
                            }

                            @Override
                            protected String computeValue() {
                                LocalDate startedAt = dateProperties.stream()
                                        .map(ObjectProperty::get)
                                        .min(LocalDate::compareTo)
                                        .orElseThrow(() -> new IllegalStateException("Record has to have at least one measurement"));
                                return Utils.formatLocalDate(startedAt);
                            }
                        }

                );

        finishedAtLabel.textProperty()
                .bind(
                        new StringBinding() {

                            {
                                bind(dateProperties.toArray(new Observable[]{}));
                            }

                            @Override
                            protected String computeValue() {
                                LocalDate finishedAt = dateProperties.stream()
                                        .map(ObjectProperty::get)
                                        .max(LocalDate::compareTo)
                                        .orElseThrow(() -> new IllegalStateException("Record has to have at least one measurement"));
                                return Utils.formatLocalDate(finishedAt);
                            }
                        }

                );
    }

    private void showBatchOfRecords() {
        recordsForName.stream()
                .filter(it -> it.getMeasurementsTotalInSecs() > 0)
                .skip(amountOfShownRecords)
                .limit(AMOUNT_OF_RECORDS_TO_SHOW_IN_A_BATCH)
                .map(SearchRecordPane::new)
                .forEach(recordsWrapperVBox.getChildren()::add);
        amountOfShownRecords += AMOUNT_OF_RECORDS_TO_SHOW_IN_A_BATCH;
    }

    @FXML
    protected void track() {
        log.log(Level.FINE, "'Track' button is clicked");
        searchState.getRecordsForChosenName().forEach(it -> it.setTracked(true));
    }

    @FXML
    protected void notTrack() {
        log.log(Level.FINE, "'Not track' button is clicked");
        searchState.getRecordsForChosenName().forEach(it -> it.setTracked(false));
    }

}