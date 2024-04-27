package time_tracker.service.report.common;

import lombok.RequiredArgsConstructor;
import time_tracker.common.annotation.NonNull;
import time_tracker.model.ReportState;

import java.util.List;

@RequiredArgsConstructor
public class StopwatchMeasurementExportDtoCustomFormatWriter {

    private final ReportState reportState;

    public void append(
            @NonNull final List<StopwatchMeasurementExportDto> measurements,
            @NonNull final StringBuilder destination) {
        measurements.forEach(measurement -> append(measurement, destination));
    }

    public void append(
            @NonNull final StopwatchMeasurementExportDto measurement,
            @NonNull final StringBuilder destination) {
        if (reportState.getShowTimeProperty().get()) {
            destination.append("\t\t").append(measurement.getStartedAt())
                    .append(" -> ").append(measurement.getStartedAt())
                    .append("=")
                    .append(measurement.getDuration())
                    .append(System.lineSeparator());
            destination.append("\t\t")
                    .append(measurement.getComment().isEmpty() ? "-" : measurement.getComment())
                    .append(System.lineSeparator());
        } else {
            if (!measurement.getComment().isEmpty()) {
                destination.append("\t\t")
                        .append(measurement.getComment())
                        .append(System.lineSeparator());
            }
        }
    }
}
