package ru.otus.jdbc.mapper.orm;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        return String.format("insert into %s%s values %s",
                tableName,
                buildFieldClosures(),
                buildValueClosures());
    }

    @Override
    public String getUpdateSql() {
        String tableName = entityClassMetaData.getName().toLowerCase();
        String idFieldName = entityClassMetaData.getIdField().getName().toLowerCase();
        return String.format("update %s set %s = %s where %s = ?",
                tableName,
                buildFieldClosures(),
                buildValueClosures(),
                idFieldName);
    }

    private String buildFieldClosures() {
        List<String> nonIdFields = entityClassMetaData.getFieldsWithoutId()
                .stream()
                .map(Field::getName)
                .toList();
        return "(" + String.join(",", nonIdFields) + ")";
    }

    private String buildValueClosures() {
        int numberOfFields = entityClassMetaData.getFieldsWithoutId().size();
        return "(" + IntStream.range(0, numberOfFields).mapToObj(x -> "?").collect(Collectors.joining(",")) + ")";
    }
}
