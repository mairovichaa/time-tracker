package time_tracker.component;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import time_tracker.common.annotation.NonNull;
import time_tracker.component.common.Icon;
import time_tracker.component.configuration.ConfigurationVBox;
import time_tracker.component.report.ReportVBox;
import time_tracker.component.search.SearchVBox;
import time_tracker.component.statistics.StatisticsVBox;
import time_tracker.component.stopwatch.StopWatchVBox;

import java.util.List;

import static time_tracker.component.Utils.load;
import static time_tracker.component.common.IconButton.initIconButton;

public class SidebarVBox extends VBox {
    public final static Double SIDEBAR_WIDTH = 200d;
    private final static Double SIDEBAR_PADDING_WIDTH = 20d;

    private final VBox workspaceVBox;
    private final StopWatchVBox stopWatchVBox = new StopWatchVBox();
    private final SearchVBox searchVBox = new SearchVBox();
    private final StatisticsVBox statisticsVBox = new StatisticsVBox();
    private final ReportVBox reportVBox = new ReportVBox();
    private final ConfigurationVBox configurationVBox = new ConfigurationVBox();

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

    public SidebarVBox(final VBox workspaceVBox) {
        load("/fxml/SidebarVBox.fxml", this);

        this.setWidth(SIDEBAR_WIDTH - SIDEBAR_PADDING_WIDTH);
        this.setMinWidth(SIDEBAR_WIDTH - SIDEBAR_PADDING_WIDTH);
        this.setPrefWidth(SIDEBAR_WIDTH - SIDEBAR_PADDING_WIDTH);

        this.workspaceVBox = workspaceVBox;
        showStopwatch();

        initIconButton(stopwatchIconLabel, 20, Icon.STOPWATCH);
        initIconButton(searchIconLabel, 20, Icon.SEARCH);
        initIconButton(statisticsIconLabel, 20, Icon.BAR_CHART);
        initIconButton(reportIconLabel, 20, Icon.SUMMARIZE);
        initIconButton(configurationIconLabel, 20, Icon.SETTINGS);
        initIconButton(logoIconLabel, 26, Icon.ALL_INCLUSIVE);
    }

    @FXML
    public void showStopwatch() {
        chooseMenuItem(stopWatchVBox, 0);

    }

    @FXML
    public void showSearch() {
        chooseMenuItem(searchVBox, 1);
    }

    @FXML
    public void showStatistics() {
        chooseMenuItem(statisticsVBox, 2);
    }

    @FXML
    public void showReport() {
        chooseMenuItem(reportVBox, 3);
    }

    @FXML
    public void showConfiguration() {
        chooseMenuItem(configurationVBox, 4);
    }

    private void chooseMenuItem(@NonNull final Node chosenElement, int menuItemIndex) {
        workspaceVBox.getChildren().clear();
        workspaceVBox.getChildren().add(chosenElement);

        List<Node> menuItems = this.getChildren()
                .stream().filter(it -> it.getStyleClass().contains("menu-item"))
                .toList();
        menuItems.forEach(it -> it.getStyleClass().remove("active-menu-item"));
        menuItems.get(menuItemIndex).getStyleClass().add("active-menu-item");
    }
}
