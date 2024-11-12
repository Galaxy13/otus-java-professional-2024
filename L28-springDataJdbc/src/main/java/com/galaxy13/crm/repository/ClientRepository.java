package com.galaxy13.crm.repository;

import com.galaxy13.crm.model.Client;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends ListCrudRepository<Client, Long> {
    Optional<Client> findClientById(Long id);

    @Override
    List<Client> findAll();

    @Modifying
    Client save(Client client);
}
