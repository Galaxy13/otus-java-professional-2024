package ru.otus.homework.processor;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.model.Message;
import ru.otus.processor.EvenSecondExceptionWrapper;
import ru.otus.processor.FieldSwapProcessor;
import ru.otus.processor.Processor;
import ru.otus.processor.SecondProvider;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvenProcessorTest {
    @Mock
    private SecondProvider provider;

    private static Stream<Arguments> generateArguments() {
        var expected = new Message.Builder(1)
                .field11("field12")
                .field12("field11")
                .build();

        var message = new Message.Builder(2)
                .field11("field11")
                .field12("field12")
                .build();

        return Stream.of(
                Arguments.of(2, message, expected),
                Arguments.of(1, message, expected)
        );
    }

    @ParameterizedTest
    @MethodSource("generateArguments")
    void testEvenProcessor(int seconds, Message message, Message expected) {
        when(provider.currentSeconds()).thenReturn(seconds);
        Processor processor = new EvenSecondExceptionWrapper(new FieldSwapProcessor(), provider);

        if (provider.currentSeconds() % 2 == 0) {
            assertThatThrownBy(() -> processor.process(message)).isInstanceOf(RuntimeException.class);
        } else {
            var actual = processor.process(message);
            assertThat(actual.getField11()).isEqualTo(expected.getField11());
            assertThat(actual.getField12()).isEqualTo(expected.getField12());
        }
    }
}
