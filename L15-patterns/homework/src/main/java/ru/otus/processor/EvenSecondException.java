package ru.otus.processor;

public class EvenSecondException extends RuntimeException {
    public EvenSecondException() {
        super("Time second is odd");
    }
}
