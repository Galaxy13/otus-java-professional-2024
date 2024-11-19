package com.galaxy13.crm.service;

import com.galaxy13.crm.model.Client;
import com.galaxy13.crm.repository.ClientRepository;
import com.galaxy13.sessionmanager.TransactionManager;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class DBServiceClientImpl implements DBServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(DBServiceClientImpl.class);

    private final ClientRepository clientRepository;
    private final TransactionManager transactionManager;

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(() -> {
            var savedClient = clientRepository.save(client);
            logger.info("Saved client {}", savedClient);
            return savedClient;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        var getClient = clientRepository.findById(id);
        logger.info("Get client {}", getClient);
        return getClient;
    }

    @Override
    public List<Client> findAll() {
        var allClients = clientRepository.findAll();
        logger.info("Find all clients {}", allClients);
        return allClients;
    }
}
