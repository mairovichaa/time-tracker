package time_tracker.component;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import time_tracker.TimeTrackerApp;
import time_tracker.common.annotation.NonNull;

import static time_tracker.component.SidebarVBox.SIDEBAR_WIDTH;

public class AppHBox extends HBox {

    private SidebarVBox sidebarVBox;
    private MFXScrollPane workspaceScrollPane = new MFXScrollPane();

    public AppHBox() {
        VBox workspaceVBox = new VBox();
        workspaceScrollPane.setContent(workspaceVBox);
        sidebarVBox = new SidebarVBox(workspaceVBox);
        this.setSpacing(20);

        this.setStyle("-fx-background-color: white;");
        this.getChildren().add(sidebarVBox);
        this.getChildren().add(workspaceScrollPane);
    }

    public void init(@NonNull final Stage primaryStage) {
        TimeTrackerApp.onMainWindowSizeChange((observable, oldValue, newValue) -> {
            double defaultMinSizeOrSizeOfWindow = Math.max(newValue.doubleValue(), 500);
            // TODO why 100???
            sidebarVBox.setMinHeight(defaultMinSizeOrSizeOfWindow - 100);
            sidebarVBox.setPrefHeight(defaultMinSizeOrSizeOfWindow - 100);
        });

        TimeTrackerApp.onMainWindowHeightSizeChange((observable, oldValue, newValue) -> {
            double defaultMinSizeOrSizeOfWindow = newValue.doubleValue();
            workspaceScrollPane.setPrefHeight(defaultMinSizeOrSizeOfWindow);
            workspaceScrollPane.setMinHeight(defaultMinSizeOrSizeOfWindow);
        });

        workspaceScrollPane.setFitToHeight(true);
        workspaceScrollPane.setFitToWidth(true);
        workspaceScrollPane.setPrefWidth(primaryStage.getScene().widthProperty().doubleValue() - SIDEBAR_WIDTH);
        workspaceScrollPane.setMinWidth(primaryStage.getScene().widthProperty().doubleValue() - SIDEBAR_WIDTH);

        TimeTrackerApp.onMainWindowWidthSizeChange((observable, oldValue, newValue) -> {
            workspaceScrollPane.setPrefWidth(newValue.doubleValue() - SIDEBAR_WIDTH);
            workspaceScrollPane.setMinWidth(newValue.doubleValue() - SIDEBAR_WIDTH);
        });
    }

    public void showStopwatch() {
        sidebarVBox.showStopwatch();
    }
}
