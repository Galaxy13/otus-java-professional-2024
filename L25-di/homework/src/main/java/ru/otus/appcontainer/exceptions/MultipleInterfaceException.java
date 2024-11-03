package ru.otus.appcontainer.exceptions;

public class MultipleInterfaceException extends RuntimeException {
    public MultipleInterfaceException(Class<?> clazz) {
        super(clazz.getName() + "  class cannot be defined uniquely due to the multiple interfaces ");
    }
}
