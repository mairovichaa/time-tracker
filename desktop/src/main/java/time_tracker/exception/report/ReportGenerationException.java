package time_tracker.exception.report;

import time_tracker.common.annotation.NonNull;

public class ReportGenerationException extends RuntimeException {

    public ReportGenerationException(@NonNull final Exception cause) {
        super(cause);
    }
}
