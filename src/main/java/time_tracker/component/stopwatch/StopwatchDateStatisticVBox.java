package time_tracker.component.stopwatch;

import javafx.beans.binding.LongBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;
import time_tracker.Utils;
import time_tracker.annotation.NonNull;
import time_tracker.config.GlobalContext;
import time_tracker.model.StopwatchRecord;
import time_tracker.service.StopwatchRecordService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static time_tracker.component.Utils.load;

@Log
public class StopwatchDateStatisticVBox extends VBox {

    @FXML
    private Label totalAmountOfTimeLabel;

    @FXML
    private Label amountOfRecordsLabel;

    public StopwatchDateStatisticVBox() {
        load("/fxml/stopwatch/StopwatchDateStatisticVBox.fxml", this);

        var stopwatchRecordService = GlobalContext.get(StopwatchRecordService.class);

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
                this.invalidate();
            }
        };

        measurementsTotalInSecsLongBindings.addListener((observable, oldValue, newValue) -> {
            log.fine("List of stopwatch records changed - rebind total");
            measurementsTotalTimeInSecs.rebind(newValue);
        });

        totalAmountOfTimeLabel.textProperty()
                .bind(new StringBinding() {
                    {
                        super.bind(measurementsTotalTimeInSecs);
                    }

                    @Override
                    protected String computeValue() {
                        return Utils.formatDuration(measurementsTotalTimeInSecs.getValue());
                    }
                });

        amountOfRecordsLabel.textProperty()
                .bind(new StringBinding() {
                    {
                        super.bind(stopwatchRecordService.findAll());
                    }

                    @Override
                    protected String computeValue() {
                        return stopwatchRecordService.findAll().size() + "";
                    }
                });
    }
}
