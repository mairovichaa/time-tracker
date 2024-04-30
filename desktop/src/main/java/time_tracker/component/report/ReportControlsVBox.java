package time_tracker.component.report;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import time_tracker.model.ReportState;

import static time_tracker.TimeTrackerApp.CONTEXT;
import static time_tracker.component.Utils.load;

public class ReportControlsVBox extends VBox {

    private final static ObservableList<String> GROUP_BY_OPTIONS = FXCollections.observableArrayList("record", "date");

    @FXML
    private MFXDatePicker startDatePicker;
    @FXML
    private MFXDatePicker endDatePicker;
    @FXML
    private MFXToggleButton showTimeToggle;
    @FXML
    private MFXComboBox<String> groupByComboBox;

    public ReportControlsVBox() {
        load("/fxml/report/ReportControlsVBox.fxml", this);

        ReportState reportState = CONTEXT.get(ReportState.class);
        startDatePicker.valueProperty().bindBidirectional(reportState.getStartDateProperty());
        endDatePicker.valueProperty().bindBidirectional(reportState.getEndDateProperty());
        showTimeToggle.selectedProperty().bindBidirectional(reportState.getShowTimeProperty());

        groupByComboBox.setItems(GROUP_BY_OPTIONS);
        if (reportState.getGroupByRecord().get()) {
            groupByComboBox.selectFirst();
        } else {
            groupByComboBox.selectIndex(1);
        }
        groupByComboBox.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            reportState.getGroupByRecord().set(newValue.equals(GROUP_BY_OPTIONS.get(0)));
        });
    }

}
