package ru.otus.jdbc.mapper.orm.exceptions;

public class OrmInvocationException extends RuntimeException {
    public OrmInvocationException(String className, Exception e) {
        super("Invocation of method in" + className + " failed", e);
    }
}
