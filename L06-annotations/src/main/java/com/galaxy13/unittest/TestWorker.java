package com.galaxy13.unittest;

import com.galaxy13.unittest.annotaions.*;
import com.galaxy13.unittest.exceptions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"java:S1854","java:S1481","java:S1066","java:S1186"})
public class TestWorker {

    Logger logger = LoggerFactory.getLogger(TestWorker.class);

    public void executeTestWork(String className) throws ClassNotFoundException,
            AnnotaionsOverrideException,
            TestConstructorException,
            MethodException {
        Class<?> clazz = Class.forName(className);
        Constructor<?> constructor;
        try {
            constructor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage());
            throw new TestConstructorException(clazz);
        }
        List<Method> before = getMethodAnnotatedWith(clazz, Before.class);
        List<Method> after = getMethodAnnotatedWith(clazz, After.class);
        List<Method> test = getMethodAnnotatedWith(clazz, Test.class);
        for (Method method : test) {
            try {
                executeTestFunction(constructor, method);
            } catch (Exception e) {
                logger.warn("{:?}", e);
            }
        }
    }

    private List<Method> getMethodAnnotatedWith(Class<?> clazz, Class<? extends Annotation> annotation) throws AnnotaionsOverrideException, MethodException {
        List<Method> methods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                if (method.getParameterCount() > 0){
                    MethodException e = new MethodException(method);
                    logger.error(e.getMessage());
                    throw e;
                }
                methods.add(method);
            }
        }
        if (annotation.isAnnotationPresent(Unique.class) && methods.size() > 1) {
                AnnotaionsOverrideException e = new AnnotaionsOverrideException(annotation);
                logger.error(e.getMessage());
                throw e;
        }
        return methods;
    }

    private void executeTestFunction(Constructor<?> constructor, Method testMethod) throws Exception{
        testMethod.setAccessible(true);
        Object testObject;
        try {
            testObject = constructor.newInstance();
        } catch (Exception e){
            throw new TestConstructorException(constructor.getDeclaringClass());
        }
        testMethod.invoke(testObject);
    }
}
