package time_tracker.component.report;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import time_tracker.Utils;
import time_tracker.config.GlobalContext;
import time_tracker.model.StopWatchAppState;
import time_tracker.model.StopwatchRecord;

import java.util.Comparator;

import static time_tracker.component.Utils.load;

public class ReportVBox extends VBox {

    @FXML
    private Label reportLabel;

    private final StopWatchAppState stopWatchAppState;

    public ReportVBox() {
        load("/fxml/report/ReportVBox.fxml", this);

        stopWatchAppState = GlobalContext.get(StopWatchAppState.class);

        stopWatchAppState.getChosenDateProperty()
                .addListener((observable, oldValue, newValue) -> rebind());
        rebind();
    }

    void rebind() {
        var chosenDate = stopWatchAppState.getChosenDateProperty().getValue();
        var records = stopWatchAppState.getDateToRecords().get(chosenDate);

        var reportContent = new StringBuilder();

        reportContent.append(chosenDate)
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        records.stream()
                .sorted(Comparator.comparing(StopwatchRecord::getName))
                .forEach(it -> {
                    var name = it.getName();
                    reportContent.append(name)
                            .append(System.lineSeparator());
                    reportContent.append(Utils.formatDuration(it.getMeasurementsTotalInSecsLongBinding().getValue()))
                            .append(System.lineSeparator());

                    it.getMeasurementsProperty()
                            .stream()
                            .filter(measurement -> !measurement.getNoteProperty().get().isBlank())
                            .map(measurement -> measurement.getNoteProperty().getValue())
                            .distinct()
                            .forEach(measurementNote ->
                                    reportContent.append("- ")
                                            .append(measurementNote)
                                            .append(System.lineSeparator())
                            );
                    reportContent.append(System.lineSeparator());
                });
        reportLabel.textProperty()
                .setValue(reportContent.toString());
    }

}
