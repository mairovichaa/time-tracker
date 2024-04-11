package time_tracker.component.search;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import time_tracker.TimeTrackerApp;
import time_tracker.common.GlobalContext;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchSearchState;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static time_tracker.Constants.AMOUNT_OF_RECORDS_TO_SHOW_IN_A_BATCH;
import static time_tracker.Constants.THRESHOLD_TO_LOAD_NEXT_BATCH;
import static time_tracker.component.Utils.load;

public class SearchVBox extends VBox {

    @FXML
    private MFXComboBox<String> trackedCombo;
    @FXML
    private MFXTextField searchInput;

    @FXML
    private VBox searchResultVBox;

    @FXML
    private MFXScrollPane searchResultScrollPane;
    @FXML
    private VBox searchWrapperVBox;

    // reference has to be stored as WeakListChangeListener is used to register it
    private final ListChangeListener<String> listChangeListener;

    private List<String> currentFoundListOfNames = Collections.emptyList();
    private int amountOfShownRecords = 0;

    public SearchVBox() {
        load("/fxml/search/SearchVBox.fxml", this);
        StopWatchAppState stopWatchAppState = GlobalContext.get(StopWatchAppState.class);
        StopwatchSearchState searchState = stopWatchAppState.getSearchState();

        ObservableList<String> trackedComboItems = FXCollections.observableArrayList("All", "Yes", "No");
        trackedCombo.setItems(trackedComboItems);
        trackedCombo.selectedItemProperty()
                .addListener((observable, oldValue, newTrackedValue) -> {
                    switch (newTrackedValue) {
                        case "All" -> searchState.getTracked().setValue(null);
                        case "Yes" -> searchState.getTracked().setValue(true);
                        case "No" -> searchState.getTracked().setValue(false);
                        default ->
                                throw new IllegalStateException("Unexpected tracked value: '" + newTrackedValue + "'.");
                    }
                });
        trackedCombo.selectIndex(0);

        searchState.getSearch()
                .bind(searchInput.textProperty());

        searchResultScrollPane.vvalueProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue.doubleValue() > THRESHOLD_TO_LOAD_NEXT_BATCH) {
                        showBatchOfRecords();
                    }
                });


        listChangeListener = c -> {
            searchResultVBox.getChildren().clear();
            amountOfShownRecords = 0;
            currentFoundListOfNames = c.getList()
                    .stream()
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            showBatchOfRecords();
        };
        searchState.getFound()
                .addListener(new WeakListChangeListener<>(listChangeListener));

        // TODO get rid of static?
        TimeTrackerApp.onMainWindowSizeChange((observable, oldValue, newValue) -> {
            double searchVBoxHeightAfterWindowResize = Math.max(newValue.doubleValue() - searchWrapperVBox.getHeight() - this.getSpacing(), 500);
            searchResultVBox.setMaxHeight(searchVBoxHeightAfterWindowResize);
            searchResultVBox.setPrefHeight(searchVBoxHeightAfterWindowResize);
        });
    }

    private void showBatchOfRecords() {
        currentFoundListOfNames.stream()
                .skip(amountOfShownRecords)
                .limit(AMOUNT_OF_RECORDS_TO_SHOW_IN_A_BATCH)
                .map(SearchResultEntryVBox::new)
                .forEach(searchResultVBox.getChildren()::add);
        amountOfShownRecords += AMOUNT_OF_RECORDS_TO_SHOW_IN_A_BATCH;
    }

}
