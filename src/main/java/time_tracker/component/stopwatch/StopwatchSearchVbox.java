package time_tracker.component.stopwatch;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.config.StopwatchSearchState;
import time_tracker.model.StopwatchRecord;

import java.util.logging.Level;

@Log
public class StopwatchSearchVbox extends VBox {

    public StopwatchSearchVbox(@NonNull final StopwatchSearchState stopwatchSearchState) {
        log.log(Level.FINE, "Create StopwatchSearchVbox");

        var searchInput = new TextField();
        stopwatchSearchState.getSearch()
                .bind(searchInput.textProperty());

        var searchHBox = new HBox(searchInput);

        var searchResultsVbox = new VBox();

        ObservableList<Node> list = FXCollections.observableArrayList();
        stopwatchSearchState.getFound()
                .addListener((ListChangeListener<StopwatchRecord>) c -> {
                    list.clear();
                    ObservableList<? extends StopwatchRecord> newSearchResults = c.getList();
                    newSearchResults.stream()
                            .map(StopwatchSearchResultEntry::new)
                            .forEach(list::add);
                });

        Bindings.bindContent(searchResultsVbox.getChildren(), list);

        this.getChildren().addAll(searchHBox, searchResultsVbox);
    }
}
