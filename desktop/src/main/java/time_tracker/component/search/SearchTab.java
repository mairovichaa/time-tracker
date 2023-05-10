package time_tracker.component.search;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;

public class SearchTab extends Tab {

    public SearchTab() {
        super("Search");
        var searchVBox = new SearchVBox();
        var recordDetailsVBox = new SearchRecordDetailsVBox();

        var hBoxWrapper = new HBox();

        hBoxWrapper.setSpacing(10);

        hBoxWrapper.getChildren()
                .addAll(searchVBox, recordDetailsVBox);
        hBoxWrapper.setPadding(new Insets(10, 0, 0, 10));
        this.setContent(hBoxWrapper);
    }
}
