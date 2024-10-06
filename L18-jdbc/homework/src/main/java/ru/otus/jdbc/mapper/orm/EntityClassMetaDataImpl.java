package ru.otus.jdbc.mapper.orm;

import ru.otus.jdbc.mapper.orm.exceptions.EntityConstructorException;
import ru.otus.jdbc.mapper.orm.exceptions.IdOverloadException;
import ru.otus.jdbc.mapper.orm.exceptions.NoIdException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final Class<T> entityClass;

    private EntityClassMetaDataImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public static <T> EntityClassMetaData<T> makeMetaData(Class<T> entityClass) {
        return new EntityClassMetaDataImpl<>(entityClass);
    }

    @Override
    public String getName() {
        return entityClass.getSimpleName();
    }

    @Override
    public Constructor<T> getConstructor() {
        try {
            return entityClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new EntityConstructorException(entityClass, e);
        }
    }

    private List<Field> getFields() {
        return List.of(entityClass.getDeclaredFields());
    }

    @Override
    public Field getIdField() {
        List<Field> idFields = getFields().stream().filter(x -> x.isAnnotationPresent(Id.class)).toList();
        if (idFields.isEmpty()) {
            throw new NoIdException("No @id annotation presented in" + entityClass.getSimpleName());
        } else if (idFields.size() > 1) {
            throw new IdOverloadException();
        }
        return idFields.getFirst();
    }

    @Override
    public List<Field> getAllFields() {
        return getFields();
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return getFields().stream().filter(x -> !x.isAnnotationPresent(Id.class)).toList();
    }
}
