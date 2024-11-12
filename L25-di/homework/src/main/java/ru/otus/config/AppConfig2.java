package ru.otus.config;

import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;
import ru.otus.services.*;

@AppComponentsContainerConfig()
public class AppConfig2 {
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
