package time_tracker.service.report.common;

import lombok.Data;

@Data
public class StopwatchMeasurementExportDto {
    private String startedAt;
    private String stoppedAt;
    private String duration;
    private String comment;
}
