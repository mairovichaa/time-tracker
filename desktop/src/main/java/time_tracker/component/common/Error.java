package time_tracker.component.common;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.NonNull;
import time_tracker.TimeTrackerApp;

import static time_tracker.component.Utils.load;

public class Error extends VBox {

    public static void showError(
            @NonNull final Exception exception
    ) {
        new Error(exception);
    }

    private final Stage dialog;

    @FXML
    private Text errorMessage;

    public Error(final @NonNull Exception exception) {
        load("/fxml/common/Error.fxml", this);

        String message = exception.getMessage();
        errorMessage.setText(message);

        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(TimeTrackerApp.primaryStage);
        dialog.setResizable(false);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("Error");

        var dialogScene = new Scene(this);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    @FXML
    protected void close() {
        dialog.close();
    }

    @FXML
    protected void copyErrorMessageToClipboard() {
        // TODO add method to time_tracker.component.Utils
        String text = errorMessage.getText();
        Clipboard clipboard = Clipboard.getSystemClipboard();
        var content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
        dialog.close();
    }
}
