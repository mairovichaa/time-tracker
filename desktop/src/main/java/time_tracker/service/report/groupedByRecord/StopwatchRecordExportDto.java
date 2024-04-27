package time_tracker.service.report.groupedByRecord;

import lombok.Data;
import time_tracker.service.report.common.StopwatchMeasurementExportDto;

import java.util.List;

@Data
public class StopwatchRecordExportDto {
    private String date;
    private List<StopwatchMeasurementExportDto> measurements;
}
