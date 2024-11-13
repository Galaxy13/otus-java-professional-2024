package com.galaxy13;

import com.galaxy13.controller.ClientRestController;
import com.galaxy13.crm.model.Address;
import com.galaxy13.crm.model.Client;
import com.galaxy13.crm.model.Phone;
import com.galaxy13.crm.service.DBServiceClient;
import com.google.gson.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientRestController.class)
@AutoConfigureMockMvc
public class ClientRestControllerTest {

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Client.class, ClientAdapter.class).create();
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

        Gson gson = new GsonBuilder().registerTypeAdapter(Client.class, new ClientAdapter()).create();
        mockMvc.perform(get("/api/client/{id}", 2L).accept("application/json; charset=utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(client)));
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
}
