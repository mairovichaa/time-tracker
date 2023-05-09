package time_tracker.component.search;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.config.GlobalContext;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchSearchState;

import static time_tracker.component.Utils.load;


@Log
public class SearchResultEntryVBox extends VBox {

    @FXML
    private Label nameLabel;

    private final String recordName;
    // reference has to be stored as WeakListChangeListener is used to register it
    private final ChangeListener<String> chosenStopwatchRecordListener;

    public SearchResultEntryVBox(@NonNull final String recordName) {
        load("/fxml/search/SearchResultEntryVBox.fxml", this);
        nameLabel.setText(recordName);
        this.recordName = recordName;

        StopWatchAppState stopWatchAppState = GlobalContext.get(StopWatchAppState.class);
        StopwatchSearchState searchState = stopWatchAppState.getSearchState();

        this.chosenStopwatchRecordListener = (observable, oldVal, newVal) -> {
            log.fine(() -> "chosen stopwatch record have been changed");

            if (recordName.equals(newVal)) {
                this.getStyleClass()
                        .add("search-result-entry-chosen");
                return;
            }
            if (recordName.equals(oldVal)) {
                this.getStyleClass()
                        .remove("search-result-entry-chosen");
            }
        };
        searchState.getChosenRecordName()
                .addListener(new WeakChangeListener<String>(chosenStopwatchRecordListener));
    }

    @FXML
    public void chooseRecord() {
        StopWatchAppState appState = GlobalContext.get(StopWatchAppState.class);
        StopwatchSearchState searchState = appState.getSearchState();
        searchState.getChosenRecordName()
                .setValue(recordName);
    }
}
