package time_tracker.component.search;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.annotation.NonNull;
import time_tracker.config.GlobalContext;
import time_tracker.model.StopwatchSearchState;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;
import time_tracker.service.StopwatchRecordSearchService;

import java.util.List;

import static java.util.Comparator.comparing;
import static time_tracker.component.Utils.load;

@Log
public class SearchRecordDetailsVBox extends VBox {

    private static final String LOG_PATTERN = "Chosen record name is changed from %s to %s";

    @NonNull
    @FXML
    private VBox searchRecordsDetailsWrapperVBox;

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

    private final StopwatchSearchState searchState;

    public SearchRecordDetailsVBox() {
        load("/fxml/search/SearchRecordDetailsVBox.fxml", this);

        var appState = GlobalContext.get(StopWatchAppState.class);
        searchState = appState.getSearchState();
        searchState.getChosenRecordName()
                .addListener((observable, oldValue, newRecordName) -> {
                    log.fine(() -> String.format(LOG_PATTERN, oldValue, newRecordName));
                    if (newRecordName != null) {
                        recordNameLabel.setText(newRecordName);

                        var stopwatchRecordSearchService = GlobalContext.get(StopwatchRecordSearchService.class);
                        var recordsForName = stopwatchRecordSearchService.recordsByName(newRecordName);
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
        refreshRecords(searchState.getRecordsForChosenName());
        searchRecordsDetailsWrapperVBox.setDisable(false);
        searchRecordsDetailsWrapperVBox.setVisible(false);
    }

    private void refreshRecords(@NonNull List<? extends StopwatchRecord> recordsForName) {
        long totalTimeInSecs = recordsForName.stream()
                .mapToLong(StopwatchRecord::getMeasurementsTotalInSecs)
                .sum();
        String formattedTotalTime = Utils.formatDuration(totalTimeInSecs);
        totalTimeLabel.setText(formattedTotalTime);

        int amountOfRecords = recordsForName.size();
        numberOfDatesLabel.setText(Integer.toString(amountOfRecords));

        recordsWrapperVBox.getChildren().clear();
        recordsForName.stream()
                .map(SearchRecordVBox::new)
                .forEach(recordsWrapperVBox.getChildren()::add);
    }
}