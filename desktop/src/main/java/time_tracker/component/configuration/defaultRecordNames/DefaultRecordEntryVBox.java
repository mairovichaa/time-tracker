package time_tracker.component.configuration.defaultRecordNames;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.extern.java.Log;
import time_tracker.service.ConfigurationService;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;

@Log
public class DefaultRecordEntryVBox extends VBox {

    @FXML
    protected Text defaultRecordNameText;
    private final String deleteDefaultRecordName;

    public DefaultRecordEntryVBox(String defaultRecordName) {
        load("/fxml/configuration/defaultRecordNames/DefaultRecordEntryVBox.fxml", this);
        this.deleteDefaultRecordName = defaultRecordName;
        defaultRecordNameText.setText(defaultRecordName);
    }

    @FXML
    protected void delete() {
        log.info(() -> "'deleteDefaultRecord' is clicked");
        var configurationService = CONTEXT.get(ConfigurationService.class);
        configurationService.deleteDefaultRecord(deleteDefaultRecordName);
    }
}
