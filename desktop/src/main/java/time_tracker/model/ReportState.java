package time_tracker.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import time_tracker.config.properties.StopwatchProperties;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class ReportState {

    private ObjectProperty<LocalDate> startDateProperty = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDateProperty = new SimpleObjectProperty<>();
    private BooleanProperty showTimeProperty = new SimpleBooleanProperty();
    private BooleanProperty groupByRecord = new SimpleBooleanProperty();
    private ObjectProperty<StopwatchProperties.ReportProperties.ExportFormat> exportFormat = new SimpleObjectProperty<>();
    private ObjectProperty<Map<String, List<StopwatchRecord>>> recordNameToRecordsProperty = new SimpleObjectProperty<>(Collections.emptyMap());
    private ObjectProperty<Map<LocalDate, List<StopwatchRecord>>> dateToRecordsProperty = new SimpleObjectProperty<>(Collections.emptyMap());

    public LocalDate getStartDate() {
        return startDateProperty.getValue();
    }

    public LocalDate getEndDate() {
        return endDateProperty.getValue();
    }

    public boolean isGroupByRecord() {
        return groupByRecord.get();
    }

    public Map<String, List<StopwatchRecord>> getRecordNameToRecords() {
        return recordNameToRecordsProperty.get();
    }

    public Map<LocalDate, List<StopwatchRecord>> getDateToRecords() {
        return dateToRecordsProperty.get();
    }
}
