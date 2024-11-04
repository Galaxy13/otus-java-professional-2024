package ru.otus.appcontainer;

import org.reflections.Reflections;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;
import ru.otus.appcontainer.api.Qualifier;
import ru.otus.appcontainer.exceptions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final Map<Class<?>, List<Object>> componentsByClass = new HashMap<>();
    private final Map<Class<?>, List<Method>> returnTypes = new HashMap<>();
    private final Map<String, Object> componentsByName = new HashMap<>();
    private final Map<String, Method> annotatedNameToMethod = new HashMap<>();
    private final Map<Class<?>, Object> baseConfigObjects = new HashMap<>();

    public AppComponentsContainerImpl(Class<?>... initialConfigClasses) {
        processConfig(initialConfigClasses);
    }

    public AppComponentsContainerImpl(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> configs = reflections.getTypesAnnotatedWith(AppComponentsContainerConfig.class);
        if (configs.isEmpty()) {
            throw new ContextSolvingError(AppComponentsContainerImpl.class, "No AppComponentsContainer config found");
        }
        processConfig(configs.toArray(new Class<?>[0]));
    }

    private void processConfig(Class<?>... initialConfigClasses) {
        for (Class<?> initialConfigClass : initialConfigClasses) {
            checkConfigClass(initialConfigClass);
        }
        fillMultipleConfigsMaps(initialConfigClasses);
        for (Method method : annotatedNameToMethod.values()) {
            if (method.isAnnotationPresent(AppComponent.class)) {
                processComponent(method, returnTypes.size());
            }
        }
    }

    private void fillMultipleConfigsMaps(Class<?>[] configClasses) {
        for (Class<?> configClass : configClasses) {
            Object appConfigInstance = initializeConfig(configClass);
            baseConfigObjects.put(configClass, appConfigInstance);
            fillMethodMaps(configClass.getMethods());
        }
    }

    private Object initializeConfig(Class<?> configClass) {
        Object appConfigInstance;
        try {
            appConfigInstance = configClass.getConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new ConfigConstructorException(configClass, e);
        }
        return appConfigInstance;
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not ctx config %s", configClass.getName()));
        }
    }

    private void processComponent(Method method, int solvingDepth) {
        if (solvingDepth < 0) {
            throw new ContextSolvingError(method.getDeclaringClass(), "Cyclic dependency exception in app config:");
        }
        if (componentsByName.containsKey(method.getAnnotation(AppComponent.class).name())) {
            return;
        }
        processMethod(method, solvingDepth);
    }

    private void processMethod(Method method, int solvingDepth) {
        Parameter[] parameters = method.getParameters();
        List<Object> paramObjects = initializeParameters(parameters, solvingDepth);
        try {
            Class<?> returnType = method.getReturnType();
            String annotatedName = method.getAnnotation(AppComponent.class).name();
            Object baseConfig = baseConfigObjects.get(method.getDeclaringClass());
            Object componentInstance = method.invoke(baseConfig, paramObjects.toArray(new Object[0]));
            componentsByClass.computeIfAbsent(returnType, k -> new ArrayList<>()).add(componentInstance);
            componentsByName.put(annotatedName, componentInstance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ComponentInvocationException(method, e);
        }
    }

    private List<Object> initializeParameters(Parameter[] parameters, int solvingDepth) {
        List<Object> paramObjects = new ArrayList<>();
        for (Parameter parameter : parameters) {
            if (returnTypes.containsKey(parameter.getType())) {
                Method parameterInitializer = getParameterMethod(parameter);
                String componentName = parameterInitializer.getAnnotation(AppComponent.class).name();
                if (!componentsByName.containsKey(componentName)) {
                    processComponent(parameterInitializer, --solvingDepth);
                }
                paramObjects.add(componentsByName.get(componentName));
            } else {
                throw new ContextSolvingError(parameter.getType(), "No method found to instantiate dependency:");
            }
        }
        return paramObjects;
    }

    private Method getParameterMethod(Parameter parameter) {
        List<Method> methods = returnTypes.get(parameter.getType());
        if (methods.size() == 1) {
            return methods.getFirst();
        }
        if (!parameter.isAnnotationPresent(Qualifier.class)) {
            throw new ContextSolvingError(parameter.getType(), "Parameter has multiple candidates. Qualifier annotation required.");
        }
        String qualifierName = parameter.getAnnotation(Qualifier.class).component();
        Method qualifierMethod = annotatedNameToMethod.get(qualifierName);
        if (qualifierMethod == null) {
            throw new ContextSolvingError(parameter.getType(), "No method found to instantiate dependency:");
        }
        return qualifierMethod;
    }

    private void fillMethodMaps(Method[] methods) {
        for (Method method : methods) {
            if (method.isAnnotationPresent(AppComponent.class)) {
                returnTypes.computeIfAbsent(method.getReturnType(), k -> new ArrayList<>()).add(method);
                String componentName = method.getAnnotation(AppComponent.class).name();
                if (!annotatedNameToMethod.containsKey(componentName)) {
                    annotatedNameToMethod.put(componentName, method);
                } else {
                    throw new IllegalArgumentException(String.format("Duplicate app component named '%s'", componentName));
                }
            }
        }
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        List<Object> appComponentInstancesList = componentsByClass.computeIfAbsent(componentClass, k -> {
            Class<?>[] interfaces = componentClass.getInterfaces();
            List<Object> innerInstance = null;
            for (Class<?> interfaceClass : interfaces) {
                if (returnTypes.containsKey(interfaceClass)) {
                    if (innerInstance == null) {
                        innerInstance = componentsByClass.get(interfaceClass);
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
        if (appComponentInstancesList.size() > 1) {
            throw new ContextSolvingError(componentClass, "Multiple beans found in context");
        }
        return componentClass.cast(appComponentInstancesList.getFirst());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> C getAppComponent(String componentName) {
        Object appComponentInstance = componentsByName.get(componentName);
        if (appComponentInstance == null) {
            throw new NoComponentException(componentName);
        }
        return (C) appComponentInstance;
    }
}
