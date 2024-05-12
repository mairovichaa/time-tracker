package time_tracker.component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import time_tracker.common.annotation.NonNull;

import java.io.IOException;

public class Utils {

    public static void load(@NonNull final String path, @NonNull final Object node) {
        FXMLLoader fxmlLoader = new FXMLLoader(node.getClass().getResource(path));
        fxmlLoader.setRoot(node);
        fxmlLoader.setController(node);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static void hide(@NonNull final Node node) {
        node.setManaged(false);
        node.setVisible(false);
    }

    public static void show(@NonNull final Node node) {
        node.setManaged(true);
        node.setVisible(true);
    }

}
