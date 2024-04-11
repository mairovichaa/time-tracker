package time_tracker.component.statistics;

import io.github.palexdev.materialfx.beans.Alignment;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import time_tracker.Utils;
import time_tracker.common.GlobalContext;
import time_tracker.model.DayData;
import time_tracker.model.StopWatchAppState;

import java.util.Comparator;

import static time_tracker.component.Utils.load;

public class StatisticsChartsVBox extends VBox {

    @FXML
    private MFXDatePicker startDatePicker;

    @FXML
    private MFXDatePicker endDatePicker;

    LineChart<String, Number> lineChart;

    public StatisticsChartsVBox() {
        load("/fxml/statistics/StatisticsChartsVBox.fxml", this);

        startDatePicker.valueProperty()
                .addListener((observable, oldValue, newValue) -> recalculateStatistics());
        startDatePicker.popupAlignmentProperty()
                .set(Alignment.of(HPos.RIGHT, VPos.BOTTOM));

        endDatePicker.valueProperty()
                .addListener((observable, oldValue, newValue) -> recalculateStatistics());
        endDatePicker.popupAlignmentProperty()
                .set(Alignment.of(HPos.RIGHT, VPos.BOTTOM));

        var xAxis = new CategoryAxis();
        var yAxis = new NumberAxis();
        lineChart = new LineChart<>(xAxis, yAxis);
        this.getChildren().add(lineChart);
        recalculateStatistics();
    }

    private void recalculateStatistics() {
        StopWatchAppState stopWatchAppState = GlobalContext.get(StopWatchAppState.class);
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Measured time");
        stopWatchAppState.getDateToDayData()
                .values()
                .stream()
                .filter(it -> {
                    if (startDatePicker.getValue() == null && endDatePicker.getValue() == null) {
                        return true;
                    }

                    if (startDatePicker.getValue() == null) {
                        return it.getDate().isBefore(endDatePicker.getValue());
                    }

                    if (endDatePicker.getValue() == null) {
                        return startDatePicker.getValue().isBefore(it.getDate());
                    }

                    return startDatePicker.getValue().isBefore(it.getDate()) &&
                            it.getDate().isBefore(endDatePicker.getValue());

                })
                .sorted(Comparator.comparing(DayData::getDate))
                .forEach(it -> {
                    String formattedLocalDate = Utils.formatLocalDate(it.getDate());
                    long totalInSecs = it.getTotalInSecs();
                    double minutes = (double) totalInSecs / 60;
                    double hours = minutes / 60;
                    XYChart.Data<String, Number> data = new XYChart.Data<>(formattedLocalDate, hours);
                    series1.getData().add(data);
                });

        lineChart.getData().clear();
        lineChart.getData().add(series1);

    }
}
