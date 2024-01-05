package time_tracker.component.configuration;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.extern.java.Log;
import time_tracker.common.GlobalContext;
import time_tracker.config.properties.AppProperties;

@Log
public class ConfigurationTab extends Tab {

    public ConfigurationTab() {
        super("Configuration");

        // TODO move to common component???
        var wrapperHBox = new HBox();
        wrapperHBox.setPadding(new Insets(10, 0, 0, 10));
        wrapperHBox.setSpacing(10);

        var wrapperVBox = new VBox();

        var defaultRecordNamesWrapperVBox = new DefaultRecordConfigurationVBox();

        ObservableList<Node> hBoxChildren = wrapperHBox.getChildren();

        AppProperties appProperties = GlobalContext.get(AppProperties.class);

        ListView<String> propsList = new ListView<>();
        propsList.getItems().setAll("dates", "defaultRecords");
        propsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                wrapperVBox.getChildren().clear();
                if (newValue.equals("defaultRecords")) {
                    wrapperVBox.getChildren().add(defaultRecordNamesWrapperVBox);
                } else {
                    wrapperVBox.getChildren().add(new Text("TODO add edit node"));
                }
            }
        });
        propsList.getSelectionModel().select(0);

        hBoxChildren.add(propsList);
        hBoxChildren.add(wrapperVBox);
        this.setContent(wrapperHBox);
    }
}
