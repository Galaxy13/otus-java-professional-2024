package ru.otus.config;

import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;
import ru.otus.appcontainer.api.Qualifier;
import ru.otus.services.*;

@AppComponentsContainerConfig()
public class AppConfig1 {
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
