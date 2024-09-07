package com.galaxy13.galaxytest.exceptions.methods;

import java.lang.reflect.Method;

public class MethodArgumentException extends RuntimeException {
    public MethodArgumentException(Method method) {
        super("Arguments restricted in test method. Found args in: " + method);
    }
}
