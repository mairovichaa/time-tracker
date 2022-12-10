package time_tracker.model.mapper;

import time_tracker.annotation.NonNull;

import java.util.Collection;
import java.util.stream.Collectors;

public interface Converter<FROM, TO> {

    @NonNull
    default Collection<TO> convert(@NonNull Collection<FROM> src) {
        return src.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @NonNull
    TO convert(@NonNull FROM src);

}
