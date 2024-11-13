package com.galaxy13;

import com.galaxy13.crm.model.Client;
import com.galaxy13.crm.repository.ClientRepository;
import com.galaxy13.crm.service.DBServiceClient;
import com.galaxy13.crm.service.DBServiceClientImpl;
import com.galaxy13.sessionmanager.TransactionManager;
import com.galaxy13.sessionmanager.TransactionManagerSpring;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DbServiceTest {

    private final TransactionManager transactionManager = new TransactionManagerSpring();
    @Mock
    private ClientRepository clientRepository;

    @Test
    void shouldReturnNoClientWithoutSaving() {
        when(clientRepository.findAll()).thenReturn(new ArrayList<>());

        DBServiceClient dbServiceClient = new DBServiceClientImpl(clientRepository, transactionManager);
        assertThat(dbServiceClient.getClient(1L)).isEmpty();
        List<Client> clients = dbServiceClient.findAll();
        assertThat(clients.size()).isEqualTo(0);
    }
}
