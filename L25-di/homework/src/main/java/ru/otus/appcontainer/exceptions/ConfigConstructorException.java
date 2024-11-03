package ru.otus.appcontainer.exceptions;

public class ConfigConstructorException extends RuntimeException {
    public ConfigConstructorException(Class<?> clazz, Exception e) {
        super("Config of class " + clazz.getName() + " has no appropriate empty constructor " + e);
    }
}
