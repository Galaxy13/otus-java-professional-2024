package ru.otus.jdbc.mapper.orm;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData {
    private final EntityClassMetaData<T> entityClassMetaData;

    private EntitySQLMetaDataImpl(EntityClassMetaData<T> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    public static <T> EntitySQLMetaData createSQLWorker(EntityClassMetaData<T> entityClassMetaData) {
        return new EntitySQLMetaDataImpl<>(entityClassMetaData);
    }

    @Override
    public String getSelectAllSql() {
        return String.format("select * from %s", entityClassMetaData.getName().toLowerCase());
    }

    @Override
    public String getSelectByIdSql() {
        return String.format("select * from %s where %s = ?",
                entityClassMetaData.getName().toLowerCase(),
                entityClassMetaData.getIdField().getName().toLowerCase());
    }

    @Override
    public String getInsertSql() {
        String tableName = entityClassMetaData.getName().toLowerCase();
        List<Field> fields = entityClassMetaData.getFieldsWithoutId();
        String columnNames = fields
                .stream()
                .map(Field::getName)
                .collect(Collectors.joining(","));
        String placeholders = fields
                .stream()
                .map(field -> "?")
                .collect(Collectors.joining(","));
        return String.format("insert into %s (%s) values (%s)",
                tableName,
                columnNames,
                placeholders);
    }

    @Override
    public String getUpdateSql() {
        String tableName = entityClassMetaData.getName().toLowerCase();
        String idFieldName = entityClassMetaData.getIdField().getName().toLowerCase();
        String setClause = entityClassMetaData.getFieldsWithoutId()
                .stream()
                .map(field -> field.getName().toLowerCase() + " = ?")
                .collect(Collectors.joining(", "));
        return String.format("update %s set %s where %s = ?",
                tableName,
                setClause,
                idFieldName);
    }
}
