package time_tracker.common.di;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanInitMethodRunner {
    public void run(final Object instance, final Bean beanAnnotation) {
        String initMethodName = beanAnnotation.initMethod();
        try {
            Class<?> instanceClass = instance.getClass();
            Method initMethod = instanceClass.getDeclaredMethod(initMethodName);
            initMethod.invoke(instance);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
