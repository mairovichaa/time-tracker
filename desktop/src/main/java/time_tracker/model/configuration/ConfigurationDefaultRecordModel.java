package time_tracker.model.configuration;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import time_tracker.common.annotation.NonNull;
import time_tracker.component.configuration.defaultRecordNames.DefaultRecordEntryVBox;

public class ConfigurationDefaultRecordModel {

    @Getter
    private final StringProperty nameProperty = new SimpleStringProperty();
    @Getter
    private final ObjectProperty<DefaultRecordEntryVBox.DisplayOption> displayProperty = new SimpleObjectProperty<>();

    @NonNull
    public String getName() {
        return nameProperty.get();
    }

    public void setName(@NonNull final String name) {
        nameProperty.setValue(name);
    }

    @NonNull
    public DefaultRecordEntryVBox.DisplayOption getDisplay() {
        return displayProperty.getValue();
    }

    public void setDisplay(@NonNull final DefaultRecordEntryVBox.DisplayOption displayOption) {
        displayProperty.setValue(displayOption);
    }

}
