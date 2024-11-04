package ru.otus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.otus.appcontainer.AppComponentsContainerImpl;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;
import ru.otus.appcontainer.api.Qualifier;
import ru.otus.config.AppConfig;
import ru.otus.services.*;

import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.*;

class AppTest {

    @DisplayName("Из контекста тремя способами должен корректно доставаться компонент с проставленными полями")
    @ParameterizedTest(name = "Достаем по: {0}")
    @CsvSource(
            value = {
                    "GameProcessor, ru.otus.services.GameProcessor",
                    "GameProcessorImpl, ru.otus.services.GameProcessor",
                    "gameProcessor, ru.otus.services.GameProcessor",
                    "IOService, ru.otus.services.IOService",
                    "IOServiceStreams, ru.otus.services.IOService",
                    "ioService, ru.otus.services.IOService",
                    "PlayerService, ru.otus.services.PlayerService",
                    "PlayerServiceImpl, ru.otus.services.PlayerService",
                    "playerService, ru.otus.services.PlayerService",
                    "EquationPreparer, ru.otus.services.EquationPreparer",
                    "EquationPreparerImpl, ru.otus.services.EquationPreparer",
                    "equationPreparer, ru.otus.services.EquationPreparer"
            })
    void shouldExtractFromContextCorrectComponentWithNotNullFields(String classNameOrBeanId, Class<?> rootClass)
            throws Exception {
        var ctx = new AppComponentsContainerImpl(AppConfig.class);

        assertThat(classNameOrBeanId).isNotEmpty();
        Object component;
        if (classNameOrBeanId.charAt(0) == classNameOrBeanId.toUpperCase().charAt(0)) {
            Class<?> gameProcessorClass = Class.forName("ru.otus.services." + classNameOrBeanId);
            assertThat(rootClass).isAssignableFrom(gameProcessorClass);

            component = ctx.getAppComponent(gameProcessorClass);
        } else {
            component = ctx.getAppComponent(classNameOrBeanId);
        }
        assertThat(component).isNotNull();
        assertThat(rootClass).isAssignableFrom(component.getClass());

        var fields = Arrays.stream(component.getClass().getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .peek(f -> f.setAccessible(true))
                .toList();

        for (var field : fields) {
            var fieldValue = field.get(component);
            assertThat(fieldValue)
                    .isNotNull()
                    .isInstanceOfAny(
                            IOService.class,
                            PlayerService.class,
                            EquationPreparer.class,
                            PrintStream.class,
                            Scanner.class);
        }
    }

    @DisplayName("В контексте не должно быть компонентов с одинаковым именем")
    @Test
    void shouldNotAllowTwoComponentsWithSameName() {
        assertThatCode(() -> new AppComponentsContainerImpl(ConfigWithTwoComponentsWithSameName.class))
                .isInstanceOf(Exception.class);
    }

    @DisplayName(
            "При попытке достать из контекста отсутствующий или дублирующийся компонент, должно выкидываться исключение")
    @Test
    void shouldThrowExceptionWhenContainerContainsMoreThanOneOrNoneExpectedComponents() {
        var ctx = new AppComponentsContainerImpl(ConfigWithTwoSameComponents.class);

        assertThatCode(() -> ctx.getAppComponent(EquationPreparer.class)).isInstanceOf(Exception.class);

        assertThatCode(() -> ctx.getAppComponent(PlayerService.class)).isInstanceOf(Exception.class);

        assertThatCode(() -> ctx.getAppComponent("equationPreparer3")).isInstanceOf(Exception.class);
    }


    @DisplayName("Создание контекста с дублирующимися компонентами")
    @Test
    void shouldCreateContextFromQualifierAndExtract() {
        var ctx = new AppComponentsContainerImpl(ConfigWithTwoSameComponentsWithQualifier.class);

        assertThatThrownBy(() -> ctx.getAppComponent(IOService.class)).isInstanceOf(Exception.class);

        IOService ioService = ctx.getAppComponent("ioService1");

        assertThat(ioService).isNotNull().isInstanceOf(IOService.class);

        assertThat(ctx.getAppComponent(PlayerService.class)).isInstanceOf(PlayerService.class);
    }

    @DisplayName("Создание контекста из множества конфигурационных классов")
    @Test
    void shouldCreateContextFromMultipleConfigs() {
        var ctx = new AppComponentsContainerImpl(Config1.class, Config2.class);

        GameProcessor gameProcessor = ctx.getAppComponent(GameProcessor.class);

        assertThat(gameProcessor).isNotNull().isInstanceOf(GameProcessor.class);

        assertThatThrownBy(() -> ctx.getAppComponent(EquationPreparer.class)).isInstanceOf(Exception.class);

        EquationPreparer equationPreparer = ctx.getAppComponent("equationPreparer");
        EquationPreparer equationPreparerAlt = ctx.getAppComponent("equationPreparerAlt");

        assertThat(equationPreparer).isNotNull().isInstanceOf(EquationPreparer.class);

        assertThat(equationPreparerAlt)
                .isNotNull()
                .isInstanceOf(EquationPreparer.class)
                .isNotSameAs(equationPreparer);

    }

    @AppComponentsContainerConfig()
    public static class ConfigWithTwoComponentsWithSameName {
        public ConfigWithTwoComponentsWithSameName() {
            // empty constructor
        }

        @AppComponent(name = "equationPreparer")
        public EquationPreparer equationPreparer1() {
            return new EquationPreparerImpl();
        }

        @AppComponent(name = "equationPreparer")
        public IOService ioService() {
            return new IOServiceStreams(System.out, System.in);
        }
    }

    @AppComponentsContainerConfig()
    public static class ConfigWithTwoSameComponents {

        @AppComponent(name = "equationPreparer1")
        public EquationPreparer equationPreparer1() {
            return new EquationPreparerImpl();
        }

        @AppComponent(name = "equationPreparer2")
        public EquationPreparer equationPreparer2() {
            return new EquationPreparerImpl();
        }
    }

    @AppComponentsContainerConfig()
    public static class ConfigWithTwoSameComponentsWithQualifier {

        @AppComponent(name = "playerService")
        public PlayerService playerService(@Qualifier(component = "ioService2") IOService ioService) {
            return new PlayerServiceImpl(ioService);
        }

        @AppComponent(name = "ioService1")
        public IOService ioService1() {
            return new IOServiceStreams(System.out, System.in);
        }

        @AppComponent(name = "ioService2")
        public IOService ioService2() {
            return new IOServiceStreams(System.out, System.in);
        }
    }

    @AppComponentsContainerConfig()
    public static class Config1 {

        @AppComponent(name = "equationPreparer")
        public EquationPreparer equationPreparer() {
            return new EquationPreparerImpl();
        }

        @AppComponent(name = "gameProcessor")
        public GameProcessor gameProcessor(
                IOService ioService, PlayerService playerService, @Qualifier(component = "equationPreparerAlt") EquationPreparer equationPreparer) {
            return new GameProcessorImpl(ioService, equationPreparer, playerService);
        }
    }

    @AppComponentsContainerConfig()
    public static class Config2 {
        @AppComponent(name = "playerService")
        public PlayerService playerService(IOService ioService) {
            return new PlayerServiceImpl(ioService);
        }


        @SuppressWarnings("squid:S106")
        @AppComponent(name = "ioService")
        public IOService ioService() {
            return new IOServiceStreams(System.out, System.in);
        }

        @AppComponent(name = "equationPreparerAlt")
        public EquationPreparer equationPreparer() {
            return new EquationPreparerImpl();
        }
    }
}
