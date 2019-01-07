package time_tracker;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TimeTrackerApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Text startTitle = new Text("Start");
        VBox startVbox = new VBox(startTitle);

        Text endTitle = new Text("End");
        VBox endVbox = new VBox(endTitle);

        HBox entriesTable = new HBox(startVbox, endVbox);

        VBox root = new VBox(entriesTable);
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
