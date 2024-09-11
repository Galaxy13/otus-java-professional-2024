package com.galaxy13.galaxytest.exceptions;

public class TestConstructorException extends RuntimeException {
    public TestConstructorException(String msg, Class<?> clazz, Exception e) {
        super(msg + clazz.getCanonicalName(), e);
    }
}
