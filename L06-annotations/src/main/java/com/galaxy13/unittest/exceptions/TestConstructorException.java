package com.galaxy13.unittest.exceptions;

public class TestConstructorException extends Exception{
    public TestConstructorException(Class<?> clazz) {
        super("No appropriate (empty) constructor found for class " + clazz.getCanonicalName());
    }
}
