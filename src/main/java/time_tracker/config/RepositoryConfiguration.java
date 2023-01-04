package time_tracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;
import time_tracker.config.properties.StopwatchProperties;
import time_tracker.model.mapper.MeasurementToStopwatchRecordMeasurementConverter;
import time_tracker.model.mapper.RecordToStopwatchRecordConverter;
import time_tracker.model.mapper.StopwatchRecordMeasurementToMeasurementConverter;
import time_tracker.model.mapper.StopwatchRecordToRecordConverter;
import time_tracker.repository.*;

import java.nio.file.Paths;
import java.util.logging.Level;

@Log
public class RepositoryConfiguration {

    @NonNull
    public DayStatisticsRepository dayStatisticsRepository(
            @NonNull final FileRepository fileRepository
    ) {
        log.log(Level.FINE, "Creating dayStatisticsRepository");
        return GlobalContext.createStoreAndReturn(
                DayStatisticsRepository.class,
                () -> new DefaultDayStatisticsRepository(fileRepository)
        );
    }

    @NonNull
    public StopwatchRecordRepository stopwatchRecordRepository(
            @NonNull final FileRepository fileRepository,
            @NonNull final StopwatchRecordToRecordConverter stopwatchRecordToRecordConverter,
            @NonNull final RecordToStopwatchRecordConverter recordToStopwatchRecordConverter
    ) {
        log.log(Level.FINE, "Creating stopwatchRecordRepository");
        return GlobalContext.createStoreAndReturn(
                StopwatchRecordRepository.class,
                () -> {
                    var result = new StopwatchRecordFileRepository(stopwatchRecordToRecordConverter, recordToStopwatchRecordConverter, fileRepository);
                    result.loadAll();
                    return result;
                }
        );
    }

    @NonNull
    public FileRepository fileRepository(
            @NonNull final StopwatchProperties stopwatchProperties,
            @NonNull final ObjectMapper objectMapper
    ) {
        log.log(Level.FINE, "Creating fileRepository");
        var folderWithData = stopwatchProperties.getFolderWithData();
        var path = Paths.get(folderWithData);
        return GlobalContext.createStoreAndReturn(
                FileRepository.class,
                () -> new FileRepository(path, objectMapper)
        );
    }

    @NonNull
    public StopwatchRecordToRecordConverter stopwatchRecordToRecordConverter() {
        log.log(Level.FINE, "Creating stopwatchRecordToRecordConverter");
        return GlobalContext.createStoreAndReturn(
                StopwatchRecordToRecordConverter.class,
                () -> {
                    var stopwatchRecordMeasurementToMeasurementConverter = new StopwatchRecordMeasurementToMeasurementConverter();
                    return new StopwatchRecordToRecordConverter(stopwatchRecordMeasurementToMeasurementConverter);
                }
        );
    }

    @NonNull
    public RecordToStopwatchRecordConverter recordToStopwatchRecordConverter() {
        log.log(Level.FINE, "Creating recordToStopwatchRecordConverter");
        return GlobalContext.createStoreAndReturn(
                RecordToStopwatchRecordConverter.class,
                () -> {
                    var measurementToStopwatchRecordMeasurementConverter = new MeasurementToStopwatchRecordMeasurementConverter();
                    return new RecordToStopwatchRecordConverter(measurementToStopwatchRecordMeasurementConverter);
                }
        );
    }
}
