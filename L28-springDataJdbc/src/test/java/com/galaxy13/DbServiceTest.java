package com.galaxy13;

import com.galaxy13.crm.model.Address;
import com.galaxy13.crm.model.Client;
import com.galaxy13.crm.model.Phone;
import com.galaxy13.crm.repository.ClientRepository;
import com.galaxy13.crm.service.DBServiceClient;
import com.galaxy13.crm.service.DBServiceClientImpl;
import com.galaxy13.sessionmanager.TransactionManager;
import com.galaxy13.sessionmanager.TransactionManagerSpring;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S5838")
class DbServiceTest {

    private final TransactionManager transactionManager = new TransactionManagerSpring();
    @Mock
    private ClientRepository clientRepository;

    @DisplayName("Должен возвращать Optional.empty если клиента с id ен существует")
    @Test
    void shouldReturnNoClientWithoutSaving() {
        when(clientRepository.findAll()).thenReturn(new ArrayList<>());

        DBServiceClient dbServiceClient = new DBServiceClientImpl(clientRepository, transactionManager);
        assertThat(dbServiceClient.getClient(1L)).isEmpty();
        List<Client> clients = dbServiceClient.findAll();
        assertThat(clients.size()).isEqualTo(0);
    }

    @DisplayName("Должен возвращать клиента с проставленными id")
    @Test
    void shouldReturnSavedClient() {
        var client = new Client(null, "client1", new Address(null, "testStreet"), Set.of(new Phone("testNumber")));
        var savedClient = new Client(1L, "client1", new Address(1L, "testStreet"), Set.of(new Phone(1L, "testNumber")));
        when(clientRepository.save(client)).thenReturn(savedClient);

        DBServiceClient dbServiceClient = new DBServiceClientImpl(clientRepository, transactionManager);
        var receivedClient = dbServiceClient.saveClient(client);
        assertThat(receivedClient).isEqualTo(savedClient);
    }
}
