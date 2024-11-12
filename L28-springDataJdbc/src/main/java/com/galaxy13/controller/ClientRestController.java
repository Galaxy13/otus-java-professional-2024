package com.galaxy13.controller;

import com.galaxy13.crm.model.Client;
import com.galaxy13.crm.service.DBServiceClient;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
public class ClientRestController {
    private final DBServiceClient dbServiceClient;

    @GetMapping("/api/client/{id}")
    public Client getClientById(@PathVariable(name = "id") long id) {
        return dbServiceClient.getClient(id).orElse(null);
    }

    @GetMapping("/api/client/all")
    public List<Client> getAllClients() {
        return dbServiceClient.findAll();
    }

    @PostMapping("/api/client/")
    public Client createClient(@RequestBody Client client) {
        return dbServiceClient.saveClient(client);
    }
}
