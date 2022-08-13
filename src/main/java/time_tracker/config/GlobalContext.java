package time_tracker.config;

import lombok.experimental.UtilityClass;
import time_tracker.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

//TODO get rid of it
@UtilityClass
public class GlobalContext {

    private Map<Class<?>, Object> CLASS_TO_INSTANCE = new HashMap<>();

    public <T> T createStoreAndReturn(@NonNull Class<T> clazz, @NonNull Supplier<? extends T> supplier) {
        T instance = supplier.get();
        CLASS_TO_INSTANCE.put(clazz, instance);
        return instance;
    }

    public <T> T createStoreAndReturn(@NonNull Supplier<? extends T> supplier) {
        T instance = supplier.get();
        CLASS_TO_INSTANCE.put(instance.getClass(), instance);
        return instance;
    }

    public <T> void put(@NonNull Class<T> clazz, @NonNull T instance) {
        CLASS_TO_INSTANCE.put(clazz, instance);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(@NonNull Class<T> clazz) {
        var result = (T) CLASS_TO_INSTANCE.get(clazz);
        return Objects.requireNonNull(result);
    }
}
