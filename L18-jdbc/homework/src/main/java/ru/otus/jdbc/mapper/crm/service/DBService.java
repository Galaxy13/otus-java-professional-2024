package ru.otus.jdbc.mapper.crm.service;


import java.util.List;
import java.util.Optional;

public interface DBService<T> {

    T save(T client);

    Optional<T> getById(long no);

    List<T> findAll();
}
