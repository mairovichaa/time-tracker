package time_tracker.service;

import time_tracker.annotation.NonNull;

public interface StopwatchMeasurementService {
    void delete(@NonNull Long id);
}
