package time_tracker.service;

import java.time.LocalDate;

public class TimeService {

    public LocalDate today() {
        return LocalDate.now();
    }
}
