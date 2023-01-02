package time_tracker.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;

import java.io.IOException;
import java.nio.file.Path;

@Log
public class FileRepository {

    @NonNull
    private final ObjectMapper mapper;
    @NonNull
    private final Path pathToDirWithData;


    public FileRepository(
            @NonNull final Path pathToDirWithData,
            @NonNull final ObjectMapper mapper
    ) {
        this.pathToDirWithData = pathToDirWithData;
        this.mapper = mapper;
    }

    @NonNull
    public <T> T get(@NonNull Class<T> tClass, @NonNull String filename) {
        try {
            var dataFile = pathToDirWithData.resolve(filename + ".json");
            return mapper.readValue(dataFile.toFile(), tClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public <T> T get(@NonNull TypeReference<T> tClass, @NonNull String filename) {
        try {
            var dataFile = pathToDirWithData.resolve(filename + ".json");
            return mapper.readValue(dataFile.toFile(), tClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(@NonNull String filename, @NonNull Object data) {
        var tmpDataFile = pathToDirWithData.resolve(filename + "-tmp.json");
        try {
            log.finest(() -> "Write data to tmp file in case of failure of write to data file - to keep original data file unchanged. File = " + tmpDataFile);
            mapper.writeValue(tmpDataFile.toFile(), data);
        } catch (IOException e) {
            log.severe(() -> "Can't write to temp file: " + tmpDataFile);
            throw new RuntimeException(e);
        }

        var dataFile = pathToDirWithData.resolve(filename + ".json");
        try {
            log.finest(() -> "Write data to data file. File = " + dataFile);
            mapper.writeValue(dataFile.toFile(), data);
        } catch (IOException e) {
            log.severe(() -> "Can't write to data file: " + dataFile);
            throw new RuntimeException(e);
        }
    }
}
