package ru.otus.crm.service;

import ru.otus.crm.model.Client;

import java.util.List;
import java.util.Optional;

public interface DBClientService {

    Client saveClient(Client entity);

    Optional<Client> getById(long id);

    List<Client> findAll();
}
