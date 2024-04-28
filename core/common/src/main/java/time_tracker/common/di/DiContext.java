package time_tracker.common.di;

import time_tracker.common.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DiContext {

    private final Map<Class<?>, Object> context = new HashMap<>();

    public <T> void register(Class<T> clazz, T object) {
        context.put(clazz, object);
    }

    public void register(Class<?> configuration) {
        Method[] declaredMethods = configuration.getDeclaredMethods();

        try {
            Object configurationInstance = configuration.getDeclaredConstructor().newInstance();
            for (final Method method : declaredMethods) {
                tryToCreate(method, configurationInstance, declaredMethods);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void tryToCreate(Method method, Object configurationInstance, Method[] declaredMethods) throws InvocationTargetException, IllegalAccessException {
        if (method.getAnnotation(Bean.class) == null) {
            return;
        }

        Parameter[] parameters = method.getParameters();
        Class<?> returnType = method.getReturnType();

        if (context.containsKey(returnType)) {
            return;
        }

        if (parameters.length == 0) {
            Object createdInstance = method.invoke(configurationInstance);
            context.put(returnType, createdInstance);
            return;
        }

        for (final Parameter parameter : parameters) {
            Class<?> parameterType = parameter.getType();
            if (!context.containsKey(parameterType)) {
                lookForClassFactoryMethodAndCreateIt(parameterType, declaredMethods, configurationInstance);
            }
        }
        Object[] arguments = Arrays.stream(parameters)
                .map(Parameter::getType)
                .map(context::get)
                .toArray();

        Object createdInstance = method.invoke(configurationInstance, arguments);
        context.put(returnType, createdInstance);
    }

    private void lookForClassFactoryMethodAndCreateIt(
            final Class<?> parameterType,
            final Method[] declaredMethods,
            final Object configurationInstance
    ) throws InvocationTargetException, IllegalAccessException {
        for (final Method method : declaredMethods) {
            Class<?> returnType = method.getReturnType();
            if (returnType == parameterType) {
                tryToCreate(method, configurationInstance, declaredMethods);
                return;
            }
        }
        throw new RuntimeException("Can't find factory method for " + parameterType);
    }

    public <T> T get(@NonNull Class<T> clazz) {
        @SuppressWarnings("unchecked")
        var result = (T) context.get(clazz);

        if (result == null) {
            throw new BeanNotFoundException(clazz);
        }
        return result;
    }

}
