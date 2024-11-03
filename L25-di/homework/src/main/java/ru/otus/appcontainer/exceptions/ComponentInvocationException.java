package ru.otus.appcontainer.exceptions;

import java.lang.reflect.Method;

public class ComponentInvocationException extends RuntimeException {
    public ComponentInvocationException(Method method, Exception e) {
        super("Dependency method instantiate invocation exception:" + method.getName(), e);
    }
}
