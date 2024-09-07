package com.galaxy13.galaxytest;

import com.galaxy13.galaxytest.annotaions.*;
import com.galaxy13.galaxytest.exceptions.TestConstructorException;
import com.galaxy13.galaxytest.exceptions.annotations.AnnotaionsOverrideException;
import com.galaxy13.galaxytest.exceptions.annotations.MultipleAnnotationsException;
import com.galaxy13.galaxytest.exceptions.methods.MethodArgumentException;
import com.galaxy13.galaxytest.exceptions.methods.MethodInvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestWorker {

    private final Logger logger = LoggerFactory.getLogger(TestWorker.class);

    public boolean executeTestWork(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        if (!clazz.isAnnotationPresent(GalaxyTest.class)) {
            return false;
        }
        TestStatistics statistics = new TestStatistics(clazz);
        Constructor<?> constructor = getClassConstructor(clazz);
        Optional<Method> before = getWrapperMethod(clazz, Before.class);
        Optional<Method> after = getWrapperMethod(clazz, After.class);
        List<Method> test = getMethodAnnotatedWith(clazz, Test.class);
        for (Method testMethod : test) {
            Object testObject = instantiateTestObject(constructor, clazz);
            try {
                if (before.isPresent()) {
                    invokeMethod(testObject, before.get());
                }
                invokeMethod(testObject, testMethod);
                if (after.isPresent()) {
                    invokeMethod(testObject, after.get());
                }
                statistics.addOkTest(testMethod.getName());
            } catch (MethodInvocationException e) {
                statistics.addFailTest(testMethod.getName(), e);
                logger.warn("Exception in test execution: {}", e.getMessage());
            }
        }
        statistics.out();
        return statistics.isFailed();
    }

    private List<Method> getMethodAnnotatedWith(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Method> methods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                if (method.getParameterCount() == 0 && method.getDeclaredAnnotations().length == 1) {
                    methods.add(method);
                } else if (method.getParameterCount() > 0) {
                    logger.error("Multiple arguments found in test method: {}", method.getName());
                    throw new MethodArgumentException(method);
                } else {
                    logger.error("Multiple annotations found in test (or wrapper) method: {}%nTest entities must be defined explicitly", method.getName());
                    throw new MultipleAnnotationsException(annotation);
                }
            }
        }
        return methods;
    }

    private void invokeMethod(Object instance, Method method) throws MethodInvocationException {
        try {
            method.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MethodInvocationException(e.getCause());
        }
    }

    private Constructor<?> getClassConstructor(Class<?> clazz) {
        try {
            return clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            logger.error("No empty constructor found for class: {}%nEmpty constructor needed to correctly instantiate test object", clazz.getName());
            throw new TestConstructorException("No appropriate 'empty' constructor for test class: ", clazz, e);
        }
    }

    private Object instantiateTestObject(Constructor<?> constructor, Class<?> clazz) {
        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Instantiation of test class: {} failed", clazz.getName());
            throw new TestConstructorException("Error while instantiating test object", clazz, e);
        }
    }

    private Optional<Method> getWrapperMethod(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Method> methods = getMethodAnnotatedWith(clazz, annotation);
        if (methods.isEmpty()) {
            return Optional.empty();
        } else if (annotation.isAnnotationPresent(Unique.class) && methods.size() > 1) {
            logger.error("More than one method with @Unique annotation found in test class: {}", clazz.getName());
            throw new AnnotaionsOverrideException(annotation);
        } else {
            return Optional.of(methods.getFirst());
        }
    }
}
