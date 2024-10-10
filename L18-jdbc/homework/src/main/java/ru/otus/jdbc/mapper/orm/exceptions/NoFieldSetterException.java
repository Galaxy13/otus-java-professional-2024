package ru.otus.jdbc.mapper.orm.exceptions;

public class NoFieldSetterException extends RuntimeException {
    public NoFieldSetterException(Exception e) {
        super("No appropriate setter", e);
    }
}
