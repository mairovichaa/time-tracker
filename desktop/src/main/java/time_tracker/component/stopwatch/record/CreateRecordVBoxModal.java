package time_tracker.component.stopwatch.record;

import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.others.FunctionalStringConverter;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.configuration.ConfigurationDefaultRecordModel;
import time_tracker.model.configuration.ConfigurationState;
import time_tracker.service.StopwatchRecordService;

import java.util.List;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;
import static time_tracker.component.configuration.defaultRecordNames.DefaultRecordEntryVBox.DisplayOption.CREATE;

@Log
public class CreateRecordVBoxModal extends VBox {

    @FXML
    private MFXTextField recordNameTextField;
    @FXML
    private MFXListView<ConfigurationDefaultRecordModel> defaultRecordNamesList;

    private final StopwatchRecordService stopwatchRecordService;
    private final Stage stage;

    public CreateRecordVBoxModal(@NonNull Stage stage) {
        load("/fxml/stopwatch/record/CreateRecordVBoxModal.fxml", this);
        this.stopwatchRecordService = CONTEXT.get(StopwatchRecordService.class);
        StopWatchAppState stopwatchAppState = CONTEXT.get(StopWatchAppState.class);
        ConfigurationState configurationState = stopwatchAppState.getConfigurationState();

        List<ConfigurationDefaultRecordModel> defaultRecordButtons = configurationState.getConfigurationDefaultRecords()
                .stream()
                .filter(it -> it.getDisplay() == CREATE)
                .toList();

        StringConverter<ConfigurationDefaultRecordModel> converter = FunctionalStringConverter.to(ConfigurationDefaultRecordModel::getName);
        defaultRecordNamesList.setPrefHeight(defaultRecordButtons.size() * 32);
        // max amount of records to show without scrolling = 6
        defaultRecordNamesList.setMaxHeight(6 * 32);
        defaultRecordNamesList.setItems(FXCollections.observableArrayList(defaultRecordButtons));
        defaultRecordNamesList.setConverter(converter);
        defaultRecordNamesList.getSelectionModel().selectionProperty()
                .addListener(
                        (MapChangeListener<Integer, ConfigurationDefaultRecordModel>) change -> {
                            if (change.wasAdded()) {
                                ConfigurationDefaultRecordModel chosenRecord = change.getValueAdded();
                                String recordName = chosenRecord.getName();
                                createRecord(recordName);
                            }
                        }
                );

        this.stage = stage;
    }

    @FXML
    private void create() {
        log.fine(() -> "addStopwatchButton is clicked");
        var stopwatchName = recordNameTextField.getText();
        createRecord(stopwatchName);
    }

    private void createRecord(@NonNull final String recordName) {
        // TODO pass date here
        stopwatchRecordService.create(recordName);
        stopwatchRecordService.store();
        stage.close();
    }

    @FXML
    private void cancel() {
        log.fine(() -> "cancelButton is clicked");
        stage.close();
    }
}
