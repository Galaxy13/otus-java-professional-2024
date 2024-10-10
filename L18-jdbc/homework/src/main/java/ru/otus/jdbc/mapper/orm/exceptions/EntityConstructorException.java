package ru.otus.jdbc.mapper.orm.exceptions;

public class EntityConstructorException extends RuntimeException {
    public EntityConstructorException(Class<?> clazz, Exception ex) {
        super("Class " + clazz.getName() + " has no public constructor with no parameters", ex);
    }
}
