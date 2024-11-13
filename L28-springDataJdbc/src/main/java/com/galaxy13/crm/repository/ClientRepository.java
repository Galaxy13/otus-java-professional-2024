package com.galaxy13.crm.repository;

import com.galaxy13.crm.model.Client;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends ListCrudRepository<Client, Long> {
    Optional<Client> findClientById(Long id);

    @Query(value = """
                    select c.id as client_id,
                   c.name as client_name,
                   a.address_id as address_id,
                   a.street as street,
                   p.phone_id as phone_id,
                   p.number as phone_number from client c
                                                     left outer join public.address a on c.id = a.address_id
                                                     left outer join phone p on p.client_id = c.id
            order by c.id""", resultSetExtractorClass = ClientResultSetExtractor.class)
    @Override
    List<Client> findAll();

    @Modifying
    Client save(Client client);
}
