package com.galaxy13.servlet;

import com.galaxy13.processor.TemplateClient;
import com.galaxy13.processor.TemplateProcessor;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.crm.service.DBClientService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminPageServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminPageServlet.class);
    private static final String ADMIN_PAGE = "clients.html";

    private final transient TemplateProcessor templateProcessor;
    private final transient DBClientService dbClientService;

    public AdminPageServlet(TemplateProcessor templateProcessor, DBClientService dbClientService) {
        this.templateProcessor = templateProcessor;
        this.dbClientService = dbClientService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html");
        List<TemplateClient> clients = dbClientService
                .findAll()
                .stream()
                .map(TemplateClient::new)
                .toList();
        Map<String, Object> data = new HashMap<>();
        data.put("clients", clients);
        String page = templateProcessor.getPage(ADMIN_PAGE, data);
        try (PrintWriter out = response.getWriter()) {
            out.write(page);
        } catch (IOException e) {
            logger.error("Error getting writer to response", e);
        }
    }
}
