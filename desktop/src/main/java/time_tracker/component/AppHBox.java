package time_tracker.component;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.fxml.FXML;
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
import static time_tracker.component.Utils.load;

public class AppHBox extends HBox {

    public enum WorkspaceItem {
        STOPWATCH,
        SEARCH,
        STATISTICS,
        REPORT,
        CONFIGURATION;
    }

    @FXML
    protected SidebarVBox sidebarVBox;
    @FXML
    protected VBox workspaceVBox;
    @FXML
    protected MFXScrollPane workspaceScrollPane;
    @FXML
    protected StopWatchVBox stopWatchVBox;
    @FXML
    protected SearchVBox searchVBox;
    @FXML
    protected StatisticsVBox statisticsVBox;
    @FXML
    protected ReportVBox reportVBox;

    @FXML
    protected ConfigurationVBox configurationVBox;

    public AppHBox() {
        load("/fxml/AppHBox.fxml", this);

        StopWatchAppState appState = CONTEXT.get(StopWatchAppState.class);
        appState.getChosenWorkspaceItemObjectProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                switch (oldValue) {
                    case STOPWATCH -> {
                        stopWatchVBox.setManaged(false);
                        stopWatchVBox.setVisible(false);
                    }
                    case SEARCH -> {
                        searchVBox.setManaged(false);
                        searchVBox.setVisible(false);
                    }
                    case STATISTICS -> {
                        statisticsVBox.setManaged(false);
                        statisticsVBox.setVisible(false);
                    }
                    case REPORT -> {
                        reportVBox.setManaged(false);
                        reportVBox.setVisible(false);
                    }
                    case CONFIGURATION -> {
                        configurationVBox.setManaged(false);
                        configurationVBox.setVisible(false);
                    }
                }
            }
            switch (newValue) {
                case STOPWATCH -> {
                    stopWatchVBox.setManaged(true);
                    stopWatchVBox.setVisible(true);
                }
                case SEARCH -> {
                    searchVBox.setManaged(true);
                    searchVBox.setVisible(true);
                }
                case STATISTICS -> {
                    statisticsVBox.setManaged(true);
                    statisticsVBox.setVisible(true);
                }
                case REPORT -> {
                    reportVBox.setManaged(true);
                    reportVBox.setVisible(true);
                }
                case CONFIGURATION -> {
                    configurationVBox.setManaged(true);
                    configurationVBox.setVisible(true);
                }
            }
        });
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
