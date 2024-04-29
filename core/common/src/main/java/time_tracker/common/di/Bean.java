package time_tracker.common.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
    String initMethod() default "";
}
