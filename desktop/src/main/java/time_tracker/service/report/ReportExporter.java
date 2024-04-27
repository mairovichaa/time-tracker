package time_tracker.service.report;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.model.ReportState;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static time_tracker.config.properties.StopwatchProperties.ReportProperties.ExportFormat.JSON;

@Log
@RequiredArgsConstructor
public class ReportExporter {

    private final ReportState reportState;
    private final ReportGenerator reportGenerator;

    public void export(@NonNull final File fileToExportTo) {
        File effectiveFile = fileToExportTo;
        if (reportState.getExportFormat().get() == JSON) {
            if (!effectiveFile.getName().endsWith(".json")) {
                log.fine(() -> "Chosen file does not have '.json' suffix. Adding it");
                effectiveFile = new File(effectiveFile.getParent(), effectiveFile.getName() + ".json");
            }
        } else {
            if (!effectiveFile.getName().endsWith(".txt")) {
                log.fine(() -> "Chosen file does not have '.txt' suffix. Adding it");
                effectiveFile = new File(effectiveFile.getParent(), effectiveFile.getName() + ".txt");
            }
        }

        try {
            String report = reportGenerator.generate(reportState);
            Files.writeString(effectiveFile.toPath(), report);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
