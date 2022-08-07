package time_tracker.component.search;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import time_tracker.config.GlobalContext;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;

import static time_tracker.component.Utils.load;

public class SearchVBox extends VBox {

    @FXML
    private MFXTextField searchInput;

    @FXML
    private VBox searchResultVBox;

    public SearchVBox() {
        load("/fxml/search/SearchVBox.fxml", this);

        var stopWatchAppState = GlobalContext.get(StopWatchAppState.class);
        var searchState = stopWatchAppState.getSearchState();
        searchState.getSearch()
                .bind(searchInput.textProperty());

        ObservableList<Node> list = FXCollections.observableArrayList();

        searchState.getFound()
                .addListener((ListChangeListener<StopwatchRecord>) c -> {
                    list.clear();
                    ObservableList<? extends StopwatchRecord> newSearchResults = c.getList();
                    newSearchResults.stream()
                            .map(ResultEntryVBox::new)
                            .forEach(list::add);

                });

        Bindings.bindContent(searchResultVBox.getChildren(), list);
    }

}
