package time_tracker.component;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import time_tracker.TimeTrackerApp;
import time_tracker.common.annotation.NonNull;
import time_tracker.component.configuration.ConfigurationVBox;
import time_tracker.component.report.ReportVBox;
import time_tracker.component.search.SearchVBox;
import time_tracker.component.statistics.StatisticsVBox;
import time_tracker.component.stopwatch.StopWatchVBox;
import time_tracker.model.StopWatchAppState;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.SidebarVBox.SIDEBAR_WIDTH;

public class AppHBox extends HBox {

    private SidebarVBox sidebarVBox;
    private MFXScrollPane workspaceScrollPane = new MFXScrollPane();

    private final StopWatchVBox stopWatchVBox = new StopWatchVBox();
    private final SearchVBox searchVBox = new SearchVBox();
    private final StatisticsVBox statisticsVBox = new StatisticsVBox();
    private final ReportVBox reportVBox = new ReportVBox();
    private final ConfigurationVBox configurationVBox = new ConfigurationVBox();

    public enum WorkspaceItem {
        STOPWATCH,
        SEARCH,
        STATISTICS,
        REPORT,
        CONFIGURATION
    }

    public AppHBox() {
        VBox workspaceVBox = new VBox();
        workspaceScrollPane.setContent(workspaceVBox);

        sidebarVBox = new SidebarVBox();
        this.setSpacing(20);

        StopWatchAppState appState = CONTEXT.get(StopWatchAppState.class);
        appState.getChosenWorkspaceItemObjectProperty().addListener((observable, oldValue, newValue) -> {
            workspaceVBox.getChildren().clear();
            switch (newValue) {
                case STOPWATCH -> workspaceVBox.getChildren().add(stopWatchVBox);
                case SEARCH -> workspaceVBox.getChildren().add(searchVBox);
                case STATISTICS -> workspaceVBox.getChildren().add(statisticsVBox);
                case REPORT -> workspaceVBox.getChildren().add(reportVBox);
                case CONFIGURATION -> workspaceVBox.getChildren().add(configurationVBox);
            }
        });

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
