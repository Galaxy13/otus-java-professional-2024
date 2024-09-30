package ru.otus.processor;

import ru.otus.model.Message;

public class EvenSecondExceptionWrapper implements Processor {
    private final SecondProvider secondProvider;
    private final Processor wrappedProcessor;

    public EvenSecondExceptionWrapper(Processor wrappedProcessor, SecondProvider secondProvider) {
        this.secondProvider = secondProvider;
        this.wrappedProcessor = wrappedProcessor;
    }

    @Override
    public Message process(Message message) {
        if (secondProvider.currentSeconds() % 2 == 0) {
            throw new EvenSecondException();
        }
        return wrappedProcessor.process(message);
    }
}
