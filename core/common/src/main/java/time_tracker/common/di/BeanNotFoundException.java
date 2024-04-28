package time_tracker.common.di;

public class BeanNotFoundException extends RuntimeException {

    public BeanNotFoundException(Class<?> beanClass) {
        super("Bean of class " + beanClass + " was not found");
    }
}
