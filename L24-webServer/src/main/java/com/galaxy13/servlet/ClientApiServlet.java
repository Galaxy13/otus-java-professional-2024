package com.galaxy13.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBClientService;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class ClientApiServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ClientApiServlet.class);
    private static final int ID_PATH_PARAM_POSITION = 1;

    private final transient DBClientService dbClientService;
    private final transient Gson gson;

    public ClientApiServlet(DBClientService dbClientService, Gson gson) {
        this.dbClientService = dbClientService;
        this.gson = gson;
    }

    @SuppressWarnings("java:S1989")
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Client client = dbClientService.getById(extractIdFromRequest(request)).orElse(null);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (ServletOutputStream out = response.getOutputStream()) {
            out.print(gson.toJson(client));
        }
    }

    @SuppressWarnings("java:S1989")
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Client client = createClient(request);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (ServletOutputStream out = response.getOutputStream()) {
            Client ormClient = dbClientService.saveClient(client);
            out.print(gson.toJson(ormClient));
        }
    }

    private long extractIdFromRequest(HttpServletRequest request) {
        String[] path = request.getPathInfo().split("/");
        String id = (path.length > 1) ? path[ID_PATH_PARAM_POSITION] : String.valueOf(-1);
        return Long.parseLong(id);
    }

    private Client createClient(HttpServletRequest request) {
        JsonObject jsonObject;
        try (Reader reader = request.getReader()) {
            jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            logger.error("Error while parsing request", e);
            return new Client();
        }
        String requestName = jsonObject.get("name").getAsString();
        String requestPhone = jsonObject.get("phone").getAsString();
        String requestAddress = jsonObject.get("address").getAsString();
        if (requestName == null) {
            return new Client();
        }
        Address address = new Address(requestAddress);
        List<Phone> phones = List.of(new Phone(requestPhone));
        return new Client(requestName, address, phones);
    }
}
