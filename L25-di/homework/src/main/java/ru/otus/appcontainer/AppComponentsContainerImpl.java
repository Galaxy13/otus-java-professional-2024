package ru.otus.appcontainer;

import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;
import ru.otus.appcontainer.exceptions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final Map<Class<?>, Object> components = new HashMap<>();
    private final Map<Class<?>, Method> returnTypes = new HashMap<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);
        Method[] configMethods = configClass.getMethods();
        fillReturnTypes(configMethods);
        Object appConfigInstance;
        try {
            appConfigInstance = configClass.getConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new ConfigConstructorException(configClass, e);
        }
        for (Method method : configMethods) {
            if (method.isAnnotationPresent(AppComponent.class)) {
                processComponent(appConfigInstance, method, returnTypes.size());
            }
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not ctx config %s", configClass.getName()));
        }
    }

    private void processComponent(Object appConfigInstance, Method method, int solvingDepth) {
        if (solvingDepth < 0) {
            throw new ContextSolvingError(appConfigInstance.getClass(), "Cyclic dependency exception in app config:");
        }
        if (components.containsKey(method.getReturnType())) {
            return;
        }
        Parameter[] parameters = method.getParameters();
        List<Object> paramObjects = new ArrayList<>();
        for (Parameter parameter : parameters) {
            if (!components.containsKey(parameter.getType())) {
                if (returnTypes.containsKey(parameter.getType())) {
                    processComponent(appConfigInstance, returnTypes.get(parameter.getType()), --solvingDepth);
                } else {
                    throw new ContextSolvingError(appConfigInstance.getClass(), "No method found to instantiate dependency:");
                }
            }
            paramObjects.add(components.get(parameter.getType()));
        }
        try {
            Class<?> returnType = method.getReturnType();
            String annotatedName = method.getAnnotation(AppComponent.class).name();
            Object componentInstance = method.invoke(appConfigInstance, paramObjects.toArray(new Object[0]));
            components.put(returnType, componentInstance);
            if (appComponentsByName.containsKey(annotatedName)) {
                throw new IllegalArgumentException(String.format("Duplicate app component named '%s'", annotatedName));
            }
            appComponentsByName.put(annotatedName, componentInstance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ComponentInvocationException(method, e);
        }
    }

    private void fillReturnTypes(Method[] methods) {
        for (Method method : methods) {
            if (method.isAnnotationPresent(AppComponent.class)) {
                if (returnTypes.containsKey(method.getReturnType())) {
                    throw new IllegalArgumentException(String.format("App config contains more than one method returning %s",
                            method.getReturnType().getName()));
                }
                returnTypes.put(method.getReturnType(), method);
            }
        }
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        Object appComponentInstance = components.computeIfAbsent(componentClass, k -> {
            Class<?>[] interfaces = componentClass.getInterfaces();
            Object innerInstance = null;
            for (Class<?> interfaceClass : interfaces) {
                if (returnTypes.containsKey(interfaceClass)) {
                    if (innerInstance == null) {
                        innerInstance = components.get(interfaceClass);
                    } else {
                        throw new MultipleInterfaceException(componentClass);
                    }
                }
            }
            if (innerInstance == null) {
                throw new NoComponentException(componentClass.getName());
            }
            return innerInstance;
        });
        return componentClass.cast(appComponentInstance);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> C getAppComponent(String componentName) {
        Object appComponentInstance = appComponentsByName.get(componentName);
        if (appComponentInstance == null) {
            throw new NoComponentException(componentName);
        }
        return (C) appComponentInstance;
    }
}
