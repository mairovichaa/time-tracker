package time_tracker.component.stopwatch;

import javafx.beans.binding.LongBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.annotation.NonNull;
import time_tracker.model.StopwatchRecord;
import time_tracker.service.StopwatchRecordService;

import java.util.List;
import java.util.stream.Collectors;

@Log
public class StopwatchDateStatisticVBox extends VBox {

    public StopwatchDateStatisticVBox(
            @NonNull final StopwatchRecordService stopwatchRecordService
    ) {

        ObjectBinding<ObservableList<LongBinding>> measurementsTotalInSecsLongBindings = new ObjectBinding<>() {
            {
                bind(stopwatchRecordService.findAll());
            }

            @Override
            protected ObservableList<LongBinding> computeValue() {
                log.fine("List of stopwatch records changed - computing new value");
                var totalInSecsBindings = stopwatchRecordService.findAll().stream()
                        .map(StopwatchRecord::getMeasurementsTotalInSecsLongBinding)
                        .collect(Collectors.toList());
                ObservableList<LongBinding> result = FXCollections.observableArrayList();
                result.addAll(totalInSecsBindings);
                return result;
            }
        };

        var measurementsTotalTimeInSecs = new LongBinding() {
            @NonNull
            private List<LongBinding> bound = measurementsTotalInSecsLongBindings.getValue();

            {
                bound.forEach(super::bind);
            }

            @Override
            protected long computeValue() {
                return bound.stream()
                        .mapToLong(LongBinding::getValue)
                        .sum();
            }

            public void rebind(@NonNull final List<LongBinding> newBindings) {
                log.fine(() -> "Old bindings " + bound + ", new bindings " + newBindings);
                bound.forEach(super::unbind);
                newBindings.forEach(super::bind);
                bound = newBindings;
            }
        };

        measurementsTotalInSecsLongBindings.addListener((observable, oldValue, newValue) -> {
            log.fine("List of stopwatch records changed - rebind total");
            measurementsTotalTimeInSecs.rebind(newValue);
        });


        var totalText = new Text();
        totalText.textProperty()
                .bind(new StringBinding() {
                    {
                        super.bind(measurementsTotalTimeInSecs);
                    }

                    @Override
                    protected String computeValue() {
                        return "Total amount of time: " + Utils.formatDuration(measurementsTotalTimeInSecs.getValue());
                    }
                });

        var amountOfRecordsText = new Text();
        amountOfRecordsText.textProperty()
                .bind(new StringBinding() {
                    {
                        super.bind(stopwatchRecordService.findAll());
                    }

                    @Override
                    protected String computeValue() {
                        return "Amount of records: " + stopwatchRecordService.findAll().size();
                    }
                });

        var dayStatisticText = new Text("Day statistic");
        this.getChildren().addAll(dayStatisticText, totalText, amountOfRecordsText);
    }
}
