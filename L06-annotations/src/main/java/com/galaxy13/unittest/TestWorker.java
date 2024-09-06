package com.galaxy13.unittest;

import com.galaxy13.unittest.annotaions.After;
import com.galaxy13.unittest.annotaions.Before;
import com.galaxy13.unittest.annotaions.Test;
import com.galaxy13.unittest.annotaions.Unique;
import com.galaxy13.unittest.exceptions.AnnotaionsOverrideException;
import com.galaxy13.unittest.exceptions.MethodException;
import com.galaxy13.unittest.exceptions.TestConstructorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestWorker {

    Logger logger = LoggerFactory.getLogger(TestWorker.class);

    public boolean executeTestWork(String className) throws ClassNotFoundException,
            AnnotaionsOverrideException,
            TestConstructorException,
            IOException {
        Class<?> clazz = Class.forName(className);
        TestStatistics statistics = new TestStatistics(clazz);
        Constructor<?> constructor = getClassConstructor(clazz);
        Optional<Method> before = getWrapperMethod(clazz, Before.class);
        Optional<Method> after = getWrapperMethod(clazz, After.class);
        List<Method> test = getMethodAnnotatedWith(clazz, Test.class);
        for (Method testMethod : test) {
            Object testObject = instantiateTestObject(constructor, clazz);
            try {
                if (before.isPresent()) {
                    before.get().invoke(testObject);
                }
                invokeMethod(testObject, testMethod);
                if (after.isPresent()) {
                    after.get().invoke(testObject);
                }
                statistics.addOkTest(testMethod.getName());
            } catch (Exception e) {
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
                methods.add(method);
            }
        }
        return methods;
    }

    private void invokeMethod(Object instance, Method method) throws MethodException {
        try {
            method.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MethodException(e.getCause());
        }
    }

    private Constructor<?> getClassConstructor(Class<?> clazz) throws TestConstructorException {
        try {
            return clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            logger.error("No such method: {}", e.getMessage());
            throw new TestConstructorException(clazz);
        }
    }

    private Object instantiateTestObject(Constructor<?> constructor, Class<?> clazz) throws TestConstructorException {
        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Error instantiating test object: {}", e.getMessage(), e);
            throw new TestConstructorException(clazz);
        }
    }

    private Optional<Method> getWrapperMethod(Class<?> clazz, Class<? extends Annotation> annotation) throws AnnotaionsOverrideException {
        List<Method> methods = getMethodAnnotatedWith(clazz, annotation);
        if (methods.isEmpty()) {
            return Optional.empty();
        } else if (annotation.isAnnotationPresent(Unique.class) && methods.size() > 1) {
                AnnotaionsOverrideException e = new AnnotaionsOverrideException(annotation);
                logger.error(e.getMessage());
                throw e;
        } else {
            return Optional.of(methods.getFirst());
        }
    }
}
