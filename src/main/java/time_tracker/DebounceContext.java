package time_tracker;

import javafx.application.Platform;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import time_tracker.annotation.NonNull;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Log
public class DebounceContext {

    @NonNull
    private final ScheduledExecutorService scheduledExecutorService;
    @NonNull
    private final Duration debouncePeriod;

    private volatile ScheduledFuture<?> currentScheduledFuture;

    public void runWithDebounce(@NonNull final Runnable runnable) {
        if (currentScheduledFuture != null) {
            log.fine(() -> "Cancel scheduled");
            currentScheduledFuture.cancel(true);
        }

        currentScheduledFuture = scheduledExecutorService.schedule(
                () -> {
                    currentScheduledFuture = null;
                    Platform.runLater(runnable);
                },
                debouncePeriod.getNano(),
                TimeUnit.NANOSECONDS);
    }
}
