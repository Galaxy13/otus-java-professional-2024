package ru.otus.homework.processor;

import org.junit.jupiter.api.Test;
import ru.otus.model.Message;
import ru.otus.processor.EvenSecondExceptionWrapper;
import ru.otus.processor.FieldSwapProcessor;
import ru.otus.processor.Processor;
import ru.otus.processor.SecondProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EvenProcessorTest {
    @Test
    public void testEvenProcessor() {
        SecondProvider exceptionProvider = () -> 2;
        SecondProvider noExceptionProvider = () -> 1;

        Processor evenProcessor = new EvenSecondExceptionWrapper(new FieldSwapProcessor(), exceptionProvider);
        Processor noExceptionEvenProcessor = new EvenSecondExceptionWrapper(new FieldSwapProcessor(), noExceptionProvider);

        var message = new Message.Builder(2)
                .field11("field11")
                .field12("field12")
                .build();

        var expected = new Message.Builder(1)
                .field11("field12")
                .field12("field11")
                .build();

        assertThatThrownBy(() -> evenProcessor.process(message)).isInstanceOf(RuntimeException.class);

        var actual = noExceptionEvenProcessor.process(message);
        assertThat(actual.getField11()).isEqualTo(expected.getField11());
        assertThat(actual.getField12()).isEqualTo(expected.getField12());
    }
}
