package time_tracker.component.common;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Labeled;
import javafx.scene.shape.SVGPath;

import java.util.List;

public class IconButton {

    public static void initIconButton(Labeled button, int size, Icon icon) {
        initIconButton(button, size, icon, List.of("icon-button", "icon-button-grey"), List.of("button-icon-grey"));
    }

    public static void initIconButton(Labeled button, int size, Icon icon, List<String> buttonStyles, List<String> pathStyles) {
        var path = new SVGPath();
        path.setContent(icon.getCode());
        Bounds stopBounds = path.getBoundsInLocal();

        double stopScaleFactor = size / Math.max(stopBounds.getWidth(), stopBounds.getHeight());
        path.setScaleX(stopScaleFactor);
        path.setScaleY(stopScaleFactor);
        path.getStyleClass().addAll(pathStyles);

        button.setPickOnBounds(true);
        button.setGraphic(path);
        button.setAlignment(Pos.CENTER);
        button.getStyleClass().addAll(buttonStyles);
        button.setMinWidth(size);
        button.setPrefWidth(size);
        button.setMaxWidth(size);
        button.setMinHeight(size);
        button.setPrefHeight(size);
        button.setMaxHeight(size);
    }
}
