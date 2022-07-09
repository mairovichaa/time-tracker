package time_tracker.component.stopwatch;

import javafx.beans.binding.StringBinding;
import javafx.scene.text.Text;
import lombok.Getter;
import time_tracker.annotation.NonNull;
import time_tracker.model.StopWatchAppState;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StopwatchDateText extends Text {
    private static final String CHOSEN_PREFIX = "-> ";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private static final String CSS_STYLE = "stopwatch-date-text";

    @NonNull
    @Getter
    private final LocalDate date;

    @NonNull
    private final String formattedDate;
    @NonNull
    private final String formattedChosenDate;

    public StopwatchDateText(@NonNull final LocalDate date, @NonNull final StopWatchAppState stopWatchAppState) {
        this.date = date;
        this.formattedDate = DATE_FORMAT.format(date);
        this.formattedChosenDate = CHOSEN_PREFIX + formattedDate;

        var chosenDateProperty = stopWatchAppState.getChosenDateProperty();
        var binding = new StringBinding() {
            {
                super.bind(chosenDateProperty);
            }

            @Override
            protected String computeValue() {
                return chosenDateProperty.get().equals(date) ? formattedChosenDate : formattedDate;
            }

        };

        this.textProperty()
                .bind(binding);

        this.getStyleClass()
                .add(CSS_STYLE);
    }
}
