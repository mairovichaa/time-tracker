package time_tracker.temp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import time_tracker.domain.Record;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ReaderTest {

    public static void main(String[] args) throws IOException {

        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        var folder = Paths.get("/home/andrey/Documents/time-tracker-dev");

        var listTypeReference = new TypeReference<List<Record>>() {
        };

        Files.list(folder)
                .peek(System.out::println)
                .peek(f -> System.out.println(f.getFileName().toString().endsWith("json")))
                .filter(file -> file.getFileName().toString().endsWith("json"))
//                .forEach(System.out::println);
                .map(p -> {
                    try {
                        return objectMapper.readValue(p.toFile(), listTypeReference);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(System.out::println);
    }
}
