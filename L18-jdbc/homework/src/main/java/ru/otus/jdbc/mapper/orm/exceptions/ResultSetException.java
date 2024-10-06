package ru.otus.jdbc.mapper.orm.exceptions;

public class ResultSetException extends RuntimeException {
    public ResultSetException(Exception e) {
        super("SQL result set error", e);
    }
}
