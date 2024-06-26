package time_tracker.component;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import time_tracker.component.common.Icon;
import time_tracker.model.StopWatchAppState;

import java.util.List;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;
import static time_tracker.component.common.IconButton.initIconButton;

public class SidebarVBox extends VBox {
    public final static Double SIDEBAR_WIDTH = 200d;
    private final static Double SIDEBAR_PADDING_WIDTH = 20d;

    @FXML
    private Label stopwatchLabel;
    @FXML
    private Label stopwatchIconLabel;
    @FXML
    private Label searchLabel;
    @FXML
    private Label searchIconLabel;
    @FXML
    private Label statisticsIconLabel;
    @FXML
    private Label reportIconLabel;
    @FXML
    private Label configurationIconLabel;
    @FXML
    private Label logoIconLabel;
    private final StopWatchAppState appState;

    public SidebarVBox() {
        load("/fxml/SidebarVBox.fxml", this);

        this.setWidth(SIDEBAR_WIDTH - SIDEBAR_PADDING_WIDTH);
        this.setMinWidth(SIDEBAR_WIDTH - SIDEBAR_PADDING_WIDTH);
        this.setPrefWidth(SIDEBAR_WIDTH - SIDEBAR_PADDING_WIDTH);

        appState = CONTEXT.get(StopWatchAppState.class);

        initIconButton(stopwatchIconLabel, 20, Icon.STOPWATCH);
        initIconButton(searchIconLabel, 20, Icon.SEARCH);
        initIconButton(statisticsIconLabel, 20, Icon.BAR_CHART);
        initIconButton(reportIconLabel, 20, Icon.SUMMARIZE);
        initIconButton(configurationIconLabel, 20, Icon.SETTINGS);
        initIconButton(logoIconLabel, 26, Icon.ALL_INCLUSIVE);

        appState.getChosenWorkspaceItemObjectProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case STOPWATCH -> chooseMenuItem(0);
                case SEARCH -> chooseMenuItem(1);
                case STATISTICS -> chooseMenuItem(2);
                case REPORT -> chooseMenuItem(3);
                case CONFIGURATION -> chooseMenuItem(4);
            }

        });
    }

    @FXML
    public void showStopwatch() {
        appState.setChosenWorkspace(AppHBox.WorkspaceItem.STOPWATCH);
    }

    @FXML
    public void showSearch() {
        appState.setChosenWorkspace(AppHBox.WorkspaceItem.SEARCH);
    }

    @FXML
    public void showStatistics() {
        appState.setChosenWorkspace(AppHBox.WorkspaceItem.STATISTICS);
    }

    @FXML
    public void showReport() {
        appState.setChosenWorkspace(AppHBox.WorkspaceItem.REPORT);
    }

    @FXML
    public void showConfiguration() {
        appState.setChosenWorkspace(AppHBox.WorkspaceItem.CONFIGURATION);
    }

    private void chooseMenuItem(final int menuItemIndex) {
        List<Node> menuItems = this.getChildren()
                .stream().filter(it -> it.getStyleClass().contains("menu-item"))
                .toList();
        menuItems.forEach(it -> it.getStyleClass().remove("active-menu-item"));
        menuItems.get(menuItemIndex).getStyleClass().add("active-menu-item");
    }
}
