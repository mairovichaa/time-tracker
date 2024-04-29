package time_tracker.common.di;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DiContextTest {

    @Test
    public void whenConfigurationIsRegisteredThenAllBeansHaveToBeCreated() {
        // given
        var diContext = new DiContext();

        // when
        diContext.register(Configuration.class);

        // then
        assertThat(diContext.get(Class1.class)).isNotNull();
        assertThat(diContext.get(Class2.class)).isNotNull();
    }

    @Test
    public void whenBeanIsSingleTonThenContextHasToReturnTheSameInstance() {
        // given
        var diContext = new DiContext();

        // when
        diContext.register(Configuration.class);

        // then
        Class1 actual = diContext.get(Class1.class);
        assertThat(actual).isNotNull();
        assertThat(diContext.get(Class1.class)).isSameAs(actual);
    }

    @Test
    public void whenMethodIsNotAnnotatedWithBeanThenItShouldNotBeCreated() {
        // given
        var diContext = new DiContext();

        // when
        diContext.register(ConfigurationWithMethodsWithoutBeanAnnotation.class);

        // then
        assertThat(diContext.get(Class1.class)).isNotNull();
        assertThatThrownBy(() -> diContext.get(Class2.class)).isInstanceOf(BeanNotFoundException.class)
                .hasMessage("Bean of class class time_tracker.common.di.DiContextTest$Class2 was not found");
    }

    public static class Configuration {

        @Bean
        public Class1 class1() {
            return new Class1();
        }

        @Bean
        public Class2 class2() {
            return new Class2();
        }
    }

    public static class Class1 {
    }

    public static class Class2 {
    }

    public static class Class3 {
    }

    public static class Class4 {
    }

    public static class Class5 {
        public int fieldForInitialization = -1;

        public void initialize() {
            fieldForInitialization = 42;
        }
    }


    @Test
    public void whenBeanRequiresDependencyThenShouldAutomaticallyCreateIt() {
        // given
        var diContext = new DiContext();

        // when
        diContext.register(Configuration2.class);

        // then
        assertThat(diContext.get(Class1.class)).isNotNull();
        assertThat(diContext.get(Class2.class)).isNotNull();
        assertThat(diContext.get(Class3.class)).isNotNull();
        assertThat(diContext.get(Class4.class)).isNotNull();
    }

    @Test
    public void whenBeanHasInitializeMethodThenItShouldBeRan() {
        // given
        var diContext = new DiContext();

        // when
        diContext.register(ConfigurationWithBeanWithInitializeMethod.class);

        // then
        Class5 actual = diContext.get(Class5.class);
        assertThat(actual).isNotNull();
        assertThat(actual.fieldForInitialization).isEqualTo(42);
    }

    @Test
    public void whenBeanHasNonExistingInitializeMethodThenItShouldFailRegistration() {
        // given
        var diContext = new DiContext();

        // when, then
        assertThatThrownBy(() -> diContext.register(ConfigurationWithBeanWithNonExistingInitializeMethod.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("java.lang.NoSuchMethodException: time_tracker.common.di.DiContextTest$Class5.doesNotExist()");
    }

    public static class Configuration2 {

        @Bean
        public Class4 class4(Class3 class3) {
            return new Class4();
        }

        @Bean
        public Class3 class3(Class2 class2) {
            return new Class3();
        }

        @Bean
        public Class2 class2(Class1 class1) {
            return new Class2();
        }

        @Bean
        public Class1 class1() {
            return new Class1();
        }
    }

    public static class ConfigurationWithMethodsWithoutBeanAnnotation {
        @Bean
        public Class1 class1() {
            return new Class1();
        }

        public Class2 class2() {
            return new Class2();
        }
    }

    public static class ConfigurationWithBeanWithInitializeMethod {
        @Bean(initMethod = "initialize")
        public Class5 class5() {
            return new Class5();
        }
    }

    public static class ConfigurationWithBeanWithNonExistingInitializeMethod {
        @Bean(initMethod = "doesNotExist")
        public Class5 class5() {
            return new Class5();
        }
    }
}