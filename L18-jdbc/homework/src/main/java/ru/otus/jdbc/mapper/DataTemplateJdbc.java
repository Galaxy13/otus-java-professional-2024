package ru.otus.jdbc.mapper;

import ru.otus.jdbc.mapper.core.repository.DataTemplate;
import ru.otus.jdbc.mapper.core.repository.executor.DbExecutor;
import ru.otus.jdbc.mapper.orm.EntityClassMetaData;
import ru.otus.jdbc.mapper.orm.EntityClassMetaDataImpl;
import ru.otus.jdbc.mapper.orm.EntitySQLMetaData;
import ru.otus.jdbc.mapper.orm.EntitySQLMetaDataImpl;
import ru.otus.jdbc.mapper.orm.exceptions.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сохратяет объект в базу, читает объект из базы
 */
@SuppressWarnings("java:S1068")
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntityClassMetaData<T> entityClassMetaData;
    private final EntitySQLMetaData entitySQLMetaData;

    public DataTemplateJdbc(DbExecutor dbExecutor, Class<T> entityClass) {
        this.dbExecutor = dbExecutor;
        this.entityClassMetaData = EntityClassMetaDataImpl.makeMetaData(entityClass);
        this.entitySQLMetaData = EntitySQLMetaDataImpl.createSQLWorker(entityClassMetaData);
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        String selectSQL = entitySQLMetaData.getSelectByIdSql();
        var fieldNames = entityClassMetaData.getAllFields().stream().map(Field::getName).toList();
        return dbExecutor.executeSelect(connection, selectSQL, List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return objectFromRs(rs, fieldNames);
                }
                return null;
            } catch (SQLException e) {
                throw new ResultSetException(e);
            }

        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        String selectAllSql = entitySQLMetaData.getSelectAllSql();
        var fieldNames = entityClassMetaData.getAllFields().stream().map(Field::getName).toList();
        return dbExecutor.executeSelect(connection, selectAllSql, new ArrayList<>(), rs -> {
            List<T> result = new ArrayList<>();
            try {
                while (rs.next()) {
                    result.add(objectFromRs(rs, fieldNames));
                }
            } catch (SQLException e) {
                throw new ResultSetException(e);
            }
            return result;
        }).orElse(new ArrayList<>());
    }

    @Override
    public long insert(Connection connection, T client) {
        String insertSQL = entitySQLMetaData.getInsertSql();
        List<String> nonIdFields = entityClassMetaData.getFieldsWithoutId().stream().map(Field::getName).toList();
        return dbExecutor.executeStatement(connection,
                insertSQL,
                getFieldValues(client, nonIdFields));
    }

    @Override
    public void update(Connection connection, T client) {
        if (!checkIfIdPresented(client)) {
            throw new NoIdException("Id parameter must be not null on update query");
        }
        String updateSQL = entitySQLMetaData.getUpdateSql();
        List<String> allFields = entityClassMetaData.getAllFields().stream().map(Field::getName).toList();
        dbExecutor.executeStatement(connection, updateSQL, getFieldValues(client, allFields));
    }

    private T createNewObject() {
        try {
            return entityClassMetaData.getConstructor().newInstance();
        } catch (Exception e) {
            throw new OrmInvocationException(entityClassMetaData.getName(), e);
        }
    }

    private void setField(T obj, String fieldName, Object value) {
        if (value == null) {
            return;
        }
        try {
            obj.getClass().getMethod("set" + capitalize(fieldName), value.getClass()).invoke(obj, value);
        } catch (NoSuchMethodException e) {
            throw new NoFieldSetterException(e);
        } catch (Exception e) {
            throw new OrmInvocationException(entityClassMetaData.getName(), e);
        }
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private Object fieldFromRs(ResultSet rs, String fieldName) {
        try {
            return rs.getObject(fieldName);
        } catch (SQLException e) {
            throw new ResultSetException(e);
        }
    }

    private List<Object> getFieldValues(T obj, List<String> nonIdValues) {
        var fieldValuesList = new ArrayList<>();
        Class<?> objClass = obj.getClass();
        try {
            for (String fieldName : nonIdValues) {
                fieldValuesList.add(objClass.getMethod("get" + capitalize(fieldName)).invoke(obj));
            }
        } catch (NoSuchMethodException e) {
            throw new NoFieldGetterException(e);
        } catch (Exception e) {
            throw new OrmInvocationException(entityClassMetaData.getName(), e);
        }
        return fieldValuesList;
    }

    private T objectFromRs(ResultSet rs, List<String> fieldNames) {
        var obj = createNewObject();
        for (String fieldName : fieldNames) {
            Object value = fieldFromRs(rs, fieldName);
            setField(obj, fieldName, value);
        }
        return obj;
    }

    private boolean checkIfIdPresented(T object) {
        String idField = entityClassMetaData.getIdField().getName();
        try {
            Object id = object.getClass().getMethod("get" + capitalize(idField)).invoke(object);
            return id != null;
        } catch (Exception e) {
            throw new NoFieldGetterException(e);
        }
    }

}
