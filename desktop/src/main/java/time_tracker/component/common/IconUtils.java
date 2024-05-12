package time_tracker.component.common;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Labeled;
import javafx.scene.shape.SVGPath;

import java.util.List;

public class IconUtils {

    public static void initIconLabeled(Labeled labeled, int size, Icon icon) {
        initIconLabeled(labeled, size, icon, List.of("icon-button", "icon-button-grey"), List.of("button-icon-grey"));
    }

    public static void initIconLabeled(Labeled labeled, int size, Icon icon, List<String> buttonStyles, List<String> pathStyles) {
        var path = new SVGPath();
        path.setContent(icon.getCode());
        Bounds stopBounds = path.getBoundsInLocal();

        double stopScaleFactor = size / Math.max(stopBounds.getWidth(), stopBounds.getHeight());
        path.setScaleX(stopScaleFactor);
        path.setScaleY(stopScaleFactor);
        path.getStyleClass().addAll(pathStyles);

        labeled.setPickOnBounds(true);
        labeled.setGraphic(path);
        labeled.setAlignment(Pos.CENTER);
        labeled.getStyleClass().addAll(buttonStyles);
        labeled.setMinWidth(size);
        labeled.setPrefWidth(size);
        labeled.setMaxWidth(size);
        labeled.setMinHeight(size);
        labeled.setPrefHeight(size);
        labeled.setMaxHeight(size);
    }
}
