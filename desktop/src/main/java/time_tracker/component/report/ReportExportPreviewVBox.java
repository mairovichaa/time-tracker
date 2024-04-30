package time_tracker.component.report;

import javafx.fxml.FXML;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import time_tracker.common.annotation.NonNull;
import time_tracker.model.ReportState;
import time_tracker.service.report.ReportGenerator;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;

public class ReportExportPreviewVBox extends VBox {

    @FXML
    private Text previewText;

    private final ReportGenerator reportGenerator;
    private final Stage stage;

    public ReportExportPreviewVBox(@NonNull final Stage stage) {
        load("/fxml/report/ReportExportPreviewVBox.fxml", this);

        this.stage = stage;
        this.reportGenerator = CONTEXT.get(ReportGenerator.class);

        var reportState = CONTEXT.get(ReportState.class);
        String report = reportGenerator.generate(reportState);
        previewText.setText(report);
    }

    @FXML
    public void closeModal() {
        stage.close();
    }

    @FXML
    public void copyToClipboard() {
        // TODO add method to time_tracker.component.Utils
        Clipboard clipboard = Clipboard.getSystemClipboard();
        var content = new ClipboardContent();
        content.putString(previewText.getText());
        clipboard.setContent(content);
        stage.close();
    }
}
