package ru.otus.appcontainer.exceptions;

public class NoComponentException extends RuntimeException {
    public NoComponentException(String className) {
        super("No component found for class " + className);
    }
}
