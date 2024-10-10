package ru.otus.jdbc.mapper.orm.exceptions;

public class NoIdException extends RuntimeException {
    public NoIdException(String message) {
        super(message);
    }
}
