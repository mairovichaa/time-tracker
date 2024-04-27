package time_tracker.service.report.groupedByDate;

import lombok.Data;
import time_tracker.service.report.common.StopwatchMeasurementExportDto;

import java.util.List;

@Data
public class StopwatchRecordExportDto {
    private String name;
    private List<StopwatchMeasurementExportDto> measurements;
}
