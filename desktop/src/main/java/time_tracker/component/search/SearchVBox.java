package time_tracker.component.search;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import time_tracker.common.GlobalContext;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;
import time_tracker.model.StopwatchSearchState;

import static time_tracker.component.Utils.load;

public class SearchVBox extends VBox {

    @FXML
    private MFXTextField searchInput;

    @FXML
    private VBox searchResultVBox;

    // reference has to be stored as WeakListChangeListener is used to register it
    private final ListChangeListener<StopwatchRecord> listChangeListener;

    public SearchVBox() {
        load("/fxml/search/SearchVBox.fxml", this);

        StopWatchAppState stopWatchAppState = GlobalContext.get(StopWatchAppState.class);
        StopwatchSearchState searchState = stopWatchAppState.getSearchState();
        searchState.getSearch()
                .bind(searchInput.textProperty());

        ObservableList<Node> list = FXCollections.observableArrayList();

        listChangeListener = c -> {
            list.clear();
            ObservableList<? extends StopwatchRecord> newSearchResults = c.getList();
            newSearchResults.stream()
                    .map(StopwatchRecord::getName)
                    .distinct()
                    .sorted()
                    .map(SearchResultEntryVBox::new)
                    .forEach(list::add);
        };
        searchState.getFound()
                .addListener(new WeakListChangeListener<>(listChangeListener));

        Bindings.bindContent(searchResultVBox.getChildren(), list);
    }

}
