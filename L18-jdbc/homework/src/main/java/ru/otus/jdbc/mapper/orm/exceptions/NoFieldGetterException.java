package ru.otus.jdbc.mapper.orm.exceptions;

public class NoFieldGetterException extends RuntimeException {
    public NoFieldGetterException(Exception e) {
        super("No appropriate getter", e);
    }
}
