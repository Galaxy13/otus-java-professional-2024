package ru.otus.crm.service;

import ru.otus.cache.HwCache;
import ru.otus.crm.model.Client;

import java.util.List;
import java.util.Optional;

public class CacheProxy implements DBClientService {
    DBClientService wrappedService;
    HwCache<Long, Client> cache;

    private CacheProxy(DBClientService wrappedService, HwCache<Long, Client> cache) {
        this.wrappedService = wrappedService;
        this.cache = cache;
    }

    public static DBClientService wrapDbService(DBClientService wrappedService, HwCache<Long, Client> cache) {
        return new CacheProxy(wrappedService, cache);
    }

    @Override
    public Client saveClient(Client client) {
        Client savedClient = wrappedService.saveClient(client);
        cache.put(savedClient.getId(), savedClient);
        return savedClient;
    }

    @Override
    public Optional<Client> getById(long id) {
        var cachedClient = cache.get(id);
        if (cachedClient.isPresent()) {
            return cachedClient;
        }
        var remoteClient = wrappedService.getById(id);
        if (remoteClient.isPresent()) {
            cache.put(id, remoteClient.get());
        } else {
            cache.remove(id);
        }
        return remoteClient;
    }

    @Override
    public List<Client> findAll() {
        var allClients = wrappedService.findAll();
        allClients.forEach(c -> cache.put(c.getId(), c));
        return allClients;
    }
}
