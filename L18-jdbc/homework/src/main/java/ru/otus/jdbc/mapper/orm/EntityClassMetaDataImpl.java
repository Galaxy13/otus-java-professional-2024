package ru.otus.jdbc.mapper.orm;

import ru.otus.jdbc.mapper.orm.exceptions.EntityConstructorException;
import ru.otus.jdbc.mapper.orm.exceptions.IdOverloadException;
import ru.otus.jdbc.mapper.orm.exceptions.NoIdException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final Class<T> entityClass;
    private final Constructor<T> constructor;
    private final Field idField;
    private final List<Field> allFields;
    private final List<Field> nonIdFields;

    private EntityClassMetaDataImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
        try {
            this.constructor = entityClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new EntityConstructorException(entityClass, e);
        }

        this.allFields = List.of(entityClass.getDeclaredFields());

        var idFieldCheck = allFields.stream().filter(x -> x.isAnnotationPresent(Id.class)).toList();
        if (idFieldCheck.isEmpty()) {
            throw new NoIdException("No @id annotation presented in" + entityClass.getSimpleName());
        } else if (idFieldCheck.size() > 1) {
            throw new IdOverloadException();
        }
        this.idField = idFieldCheck.getFirst();

        this.nonIdFields = this.allFields.stream().filter(x -> !x.isAnnotationPresent(Id.class)).toList();
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
        return this.constructor;
    }


    @Override
    public Field getIdField() {
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        return this.allFields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return this.nonIdFields;
    }
}
