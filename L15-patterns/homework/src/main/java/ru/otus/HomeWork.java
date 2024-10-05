package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.handler.ComplexProcessor;
import ru.otus.handler.Handler;
import ru.otus.listener.Listener;
import ru.otus.listener.ListenerPrinterConsole;
import ru.otus.listener.homework.HistoryListenerImpl;
import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;
import ru.otus.processor.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("java:S125")
public class HomeWork {

    private static final Logger logger = LoggerFactory.getLogger(HomeWork.class);

    /*
    Реализовать to do:
      1. Добавить поля field11 - field13 (для field13 используйте класс ObjectForMessage)
      2. Сделать процессор, который поменяет местами значения field11 и field12
      3. Сделать процессор, который будет выбрасывать исключение в четную секунду (сделайте тест с гарантированным результатом)
            Секунда должна определяьться во время выполнения.
            Тест - важная часть задания
            Обязательно посмотрите пример к паттерну Мементо!
      4. Сделать Listener для ведения истории (подумайте, как сделать, чтобы сообщения не портились)
         Уже есть заготовка - класс HistoryListener, надо сделать его реализацию
         Для него уже есть тест, убедитесь, что тест проходит
    */

    public static void main(String[] args) {
        /*
          по аналогии с Demo.class
          из элеменов "to do" создать new ComplexProcessor и обработать сообщение
        */
        SecondProvider realProvider = () -> LocalDateTime.now().getSecond();
//        SecondProvider exceptionProvider = () -> 0;
//        SecondProvider noExceptionProvider = () -> 1;

        List<Processor> processors = List.of(new FieldSwapProcessor(),
                new EvenSecondExceptionWrapper(new ProcessorUpperField10(), realProvider));

        var historyListener = new HistoryListenerImpl();
        var printListener = new ListenerPrinterConsole();
        var listeners = List.of(printListener, historyListener);

        var complexProcessor = createComplexProcessor(processors,
                listeners,
                ex -> logger.error("Exception in inner processor:", ex));

        var testObjForMsg = createObjectForMsg("obj1", "obj2", "obj3");

        var message = new Message.Builder(1L)
                .field1("field1")
                .field2("field2")
                .field3("field3")
                .field6("field6")
                .field10("field10")
                .field11("field11")
                .field13(testObjForMsg)
                .build();

        var result = complexProcessor.handle(message);
        var msgClone = historyListener.findMessageById(1L).orElse(new Message.Builder(0L).build());

        logger.info("result:{}", result);
        logger.info("msg clone:{}", msgClone);
        logger.info("Original message is equal to cloned msg: {}", msgClone.equals(result));
        logger.info("13 field is the same object: {}", msgClone.getField13() == result.getField13());
        logger.info("list of 13 field is the same object: {}", msgClone.getField13().getData() == result.getField13().getData());
    }

    private static Handler createComplexProcessor(List<Processor> processors, List<Listener> listeners, Consumer<Exception> consumer) {
        var complexProcessor = new ComplexProcessor(processors, consumer);
        for (Listener listener : listeners) {
            complexProcessor.addListener(listener);
        }
        return complexProcessor;
    }

    private static ObjectForMessage createObjectForMsg(String... args) {
        var obj = new ObjectForMessage();
        obj.setData(List.of(args));
        return obj;
    }
}
