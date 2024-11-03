package ru.otus.appcontainer.exceptions;

public class ContextSolvingError extends RuntimeException {
    public ContextSolvingError(Class<?> clazz, String message) {
        super(message + clazz.getName());
    }
}
