package ru.otus.jdbc.mapper.orm.exceptions;

public class IdOverloadException extends RuntimeException {
    public IdOverloadException() {
        super("Multiple @Id annotations are prohibited (@Id field is a primary key)");
    }
}
