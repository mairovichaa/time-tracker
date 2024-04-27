package time_tracker.component.report;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import lombok.extern.java.Log;
import time_tracker.TimeTrackerApp;
import time_tracker.common.GlobalContext;
import time_tracker.component.common.DialogFactory;
import time_tracker.component.common.Error;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.ReportState;
import time_tracker.service.report.ReportExporter;

import java.io.File;

import static time_tracker.component.Utils.load;
import static time_tracker.config.properties.StopwatchProperties.ReportProperties.ExportFormat.CUSTOM;
import static time_tracker.config.properties.StopwatchProperties.ReportProperties.ExportFormat.JSON;

@Log
public class ReportExportControlsVBox extends VBox {

    private final static ObservableList<String> FORMAT_OPTIONS = FXCollections.observableArrayList("json", "custom");
    @FXML
    private MFXComboBox<String> formatComboBox;

    private final ReportState reportState;
    private final ReportExporter reportExporter;

    public ReportExportControlsVBox() {
        load("/fxml/report/ReportExportControlsVBox.fxml", this);

        reportState = GlobalContext.get(ReportState.class);
        reportExporter = GlobalContext.get(ReportExporter.class);
        formatComboBox.setItems(FORMAT_OPTIONS);
        if (reportState.getExportFormat().get() == JSON) {
            formatComboBox.selectFirst();
        } else {
            formatComboBox.selectIndex(1);
        }
        formatComboBox.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            StopwatchProperties.ReportProperties.ExportFormat exportFormat = newValue.equals(FORMAT_OPTIONS.get(0)) ? JSON : CUSTOM;
            reportState.getExportFormat().set(exportFormat);
        });
    }

    @FXML
    public void export() {
        log.fine("'Export' button is clicked");
        var fileChooser = new FileChooser();
        File chosenFile = fileChooser.showSaveDialog(TimeTrackerApp.primaryStage);
        if (chosenFile != null) {
            String chosenFileAbsolutePath = chosenFile.getAbsolutePath();
            log.fine(() -> "Chosen file " + chosenFileAbsolutePath);

            try {
                reportExporter.export(chosenFile);
            } catch (Exception e) {
                Error.showError(e);
            }
        }
    }

    @FXML
    public void preview() {
        log.fine("'Preview' button is clicked");
        try {
            DialogFactory.createAndShow(ReportExportPreviewVBox::new, "Report preview");
        } catch (Exception e) {
            Error.showError(e);
        }
    }
}
