package time_tracker.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import time_tracker.common.annotation.NonNull;
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
        return new DefaultDayStatisticsRepository(fileRepository);
    }

    @NonNull
    public StopwatchRecordRepository stopwatchRecordRepository(@NonNull final FileRepository fileRepository) {
        log.log(Level.FINE, "Creating stopwatchRecordRepository");
        return new StopwatchRecordFileRepository(fileRepository);
    }

    @NonNull
    public FileRepository fileRepository(
            @NonNull final String folderWithData,
            @NonNull final ObjectMapper objectMapper
    ) {
        log.log(Level.FINE, "Creating fileRepository");
        var path = Paths.get(folderWithData);
        return new FileRepository(path, objectMapper);
    }

}
