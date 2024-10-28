package com.galaxy13.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DBClientService;

import java.io.IOException;

public class ClientServlet extends HttpServlet {
    private static final int ID_PATH_PARAM_POSITION = 1;

    private final transient DBClientService dbClientService;
    private final transient Gson gson;

    public ClientServlet(DBClientService dbClientService, Gson gson) {
        this.dbClientService = dbClientService;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Client client = dbClientService.getById(extractIdFromRequest(request)).orElse(null);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.print(gson.toJson(client));
    }

    private long extractIdFromRequest(HttpServletRequest request) {
        String[] path = request.getPathInfo().split("/");
        String id = (path.length > 1) ? path[ID_PATH_PARAM_POSITION] : String.valueOf(-1);
        return Long.parseLong(id);
    }
}
