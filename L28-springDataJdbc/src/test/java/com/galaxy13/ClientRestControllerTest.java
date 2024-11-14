package com.galaxy13;

import com.galaxy13.controller.ClientRestController;
import com.galaxy13.crm.model.Address;
import com.galaxy13.crm.model.Client;
import com.galaxy13.crm.model.Phone;
import com.galaxy13.crm.service.DBServiceClient;
import com.google.gson.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientRestController.class)
@AutoConfigureMockMvc
class ClientRestControllerTest {
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Client.class, new ClientAdapter()).create();

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DBServiceClient dbServiceClient;

    @DisplayName("Должен возвращать корректного клиента по id")
    @Test
    void shouldReturnClientByUId() throws Exception {
        Client client = new Client(2L,
                "testName",
                new Address(1L, "testStreet"),
                Set.of(new Phone("1111")));
        given(dbServiceClient.getClient(2L)).willReturn(Optional.of(client));

        mockMvc.perform(get("/api/client/{id}", 2L).accept("application/json; charset=utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(client)));
    }

    @DisplayName("Должен сохраять клиентов и возвращать по запросу")
    @Test
    void shouldSaveAndReturnClients() throws Exception {
        var firstPostClient = new Client(null, "client1", new Address("street1"), Set.of(new Phone("1111")));
        var secondPostClient = new Client(null, "client2", new Address("street2"), Set.of(new Phone("2222")));
        var firstReturnClient = new Client(1L, "client1", new Address(1L, "street1"), Set.of(new Phone(1L, "1111")));
        var secondReturnClient = new Client(2L, "client2", new Address(2L, "street2"), Set.of(new Phone(2L, "2222")));

        doReturn(firstReturnClient).when(dbServiceClient).saveClient(argThat(new ClientMatcher(firstPostClient)));
        doReturn(secondReturnClient).when(dbServiceClient).saveClient(argThat(new ClientMatcher(secondPostClient)));

        given(dbServiceClient.findAll()).willReturn(List.of(firstReturnClient, secondReturnClient));
        given(dbServiceClient.getClient(1L)).willReturn(Optional.of(firstPostClient));
        given(dbServiceClient.getClient(2L)).willReturn(Optional.of(secondPostClient));

        mockMvc.perform(post("/api/client/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(firstPostClient)))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(firstReturnClient)));

        mockMvc.perform(post("/api/client/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(secondPostClient)))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(secondReturnClient)));

        mockMvc.perform(get("/api/client/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(List.of(firstReturnClient, secondReturnClient))));
    }

    private static class ClientAdapter implements JsonSerializer<Client> {
        @Override
        public JsonElement serialize(Client client, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", client.getId());
            jsonObject.addProperty("name", client.getName());
            jsonObject.addProperty("address", client.getAddress().getStreet());
            JsonArray jsonArray = new JsonArray();
            client.getPhones().forEach(phone -> jsonArray.add(phone.getNumber()));
            jsonObject.add("phones", jsonArray);

            return jsonObject;
        }
    }

    private static class ClientMatcher implements ArgumentMatcher<Client> {
        private final Client client;

        public ClientMatcher(Client client) {
            this.client = client;
        }

        @Override
        public boolean matches(Client secondClient) {
            if (secondClient == null) {
                return false;
            }
            var idEquals = Objects.equals(client.getId(), secondClient.getId());
            var nameEquals = Objects.equals(client.getName(), secondClient.getName());
            boolean addressEquals;
            if (secondClient.getAddress() != null && client.getAddress().getStreet() != null) {
                addressEquals = Objects.equals(secondClient.getAddress().getStreet(), client.getAddress().getStreet());
            } else {
                addressEquals = Objects.equals(client.getAddress(), secondClient.getAddress())
                        && Objects.equals(client.getId(), secondClient.getId());
            }
            boolean phoneEquals;
            if (secondClient.getPhones() != null && client.getPhones() != null) {
                phoneEquals = secondClient.getPhones().size() == client.getPhones().size();
            } else {
                phoneEquals = Objects.equals(client.getPhones(), secondClient.getPhones());
            }
            return idEquals && nameEquals && addressEquals && phoneEquals;
        }
    }
}
