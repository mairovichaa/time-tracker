package time_tracker.component.common;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import time_tracker.TimeTrackerApp;

import java.util.concurrent.CompletableFuture;

import static time_tracker.component.Utils.load;

public class Confirmation extends VBox {

    public static CompletableFuture<Boolean> requireConfirmation() {
        return new Confirmation().getConfirmationFuture();
    }

    private final Stage dialog;
    @Getter
    private final CompletableFuture<Boolean> confirmationFuture = new CompletableFuture<>();

    public Confirmation() {
        load("/fxml/common/Confirmation.fxml", this);

        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(TimeTrackerApp.primaryStage);

        var dialogScene = new Scene(this, 200, 150);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    @FXML
    protected void confirm() {
        confirmationFuture.complete(true);
        dialog.close();
    }

    @FXML
    protected void doNotConfirm() {
        confirmationFuture.complete(false);
        dialog.close();
    }
}
