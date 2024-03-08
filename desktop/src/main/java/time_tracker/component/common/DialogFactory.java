package time_tracker.component.common;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import time_tracker.TimeTrackerApp;
import time_tracker.common.annotation.NonNull;

import java.util.function.Function;

public class DialogFactory {

    public static void createAndShow(
            @NonNull final Function<Stage, Parent> rootProvider,
            @NonNull final String title
    ) {
        var stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.initOwner(TimeTrackerApp.primaryStage);
        stage.setResizable(false);

        Parent root = rootProvider.apply(stage);

        var scene = new Scene(root);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
}
