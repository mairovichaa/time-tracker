package time_tracker.domain;

import lombok.Data;

import java.util.List;

@Data
public class AppData {
    private List<DayStatistics> dayStatistics;
    private List<Record> records;
}
