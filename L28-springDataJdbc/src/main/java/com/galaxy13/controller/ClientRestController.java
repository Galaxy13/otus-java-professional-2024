package com.galaxy13.controller;

import com.galaxy13.crm.dto.ClientDTO;
import com.galaxy13.crm.model.Client;
import com.galaxy13.crm.service.DBServiceClient;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
public class ClientRestController {
    private final DBServiceClient dbServiceClient;

    @GetMapping("/api/client/{id}")
    public ClientDTO getClientById(@PathVariable(name = "id") long id) {
        Optional<Client> client = dbServiceClient.getClient(id);
        return client.map(ClientDTO::new).orElse(null);
    }

    @GetMapping("/api/client/all")
    public List<ClientDTO> getAllClients() {
        List<Client> allClients = dbServiceClient.findAll();
        return allClients.stream().map(ClientDTO::new).toList();
    }

    @PostMapping("/api/client/")
    public ClientDTO createClient(@RequestBody Client client) {
        Client savedClient = dbServiceClient.saveClient(client);
        return new ClientDTO(savedClient);
    }
}
