package time_tracker;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TimeTrackerApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox root = new VBox();


        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
