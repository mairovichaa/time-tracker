package time_tracker.component.configuration.defaultRecordNames;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.model.configuration.ConfigurationDefaultRecordModel;
import time_tracker.service.ConfigurationService;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;

@Log
public class DefaultRecordEntryVBox extends VBox {

    public enum DisplayOption {
        ALWAYS, CREATE, NO
    }

    private final static ObservableList<String> DISPLAY_BY_DEFAULT_OPTIONS = FXCollections.observableArrayList("no", "create", "always");

    @FXML
    protected Text defaultRecordNameText;
    @FXML
    protected MFXComboBox<String> displayByDefaultComboBox;
    private final String deleteDefaultRecordName;
    private final ConfigurationDefaultRecordModel record;
    private final ConfigurationService configurationService = CONTEXT.get(ConfigurationService.class);


    public DefaultRecordEntryVBox(@NonNull final ConfigurationDefaultRecordModel record) {
        load("/fxml/configuration/defaultRecordNames/DefaultRecordEntryVBox.fxml", this);
        this.record = record;
        this.deleteDefaultRecordName = record.getName();
        defaultRecordNameText.textProperty()
                .bind(record.getNameProperty());

        displayByDefaultComboBox.setItems(DISPLAY_BY_DEFAULT_OPTIONS);
        switch (record.getDisplay()) {
            case NO -> displayByDefaultComboBox.selectIndex(0);
            case CREATE -> displayByDefaultComboBox.selectIndex(1);
            case ALWAYS -> displayByDefaultComboBox.selectIndex(2);
            default -> throw new RuntimeException();
        }

        displayByDefaultComboBox.selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    DisplayOption option = switch (newValue) {
                        case "no" -> DisplayOption.NO;
                        case "create" -> DisplayOption.CREATE;
                        case "always" -> DisplayOption.ALWAYS;
                        default -> throw new RuntimeException();
                    };

                    configurationService.changeRecordDisplay(deleteDefaultRecordName, option);
                });
    }

    @FXML
    protected void delete() {
        log.info(() -> "'deleteDefaultRecord' is clicked");
        configurationService.deleteDefaultRecord(deleteDefaultRecordName);
    }
}
