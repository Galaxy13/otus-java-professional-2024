package com.galaxy13.server;

import com.galaxy13.helpers.FileSystemHelper;
import com.galaxy13.processor.TemplateProcessor;
import com.galaxy13.server.exception.ServerJoinException;
import com.galaxy13.server.exception.ServerStartException;
import com.galaxy13.server.exception.ServerStopException;
import com.galaxy13.servlet.ClientServlet;
import com.google.gson.Gson;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.crm.service.DBClientService;

public class ClientWebServerImpl implements ClientWebServer {
    private static final Logger logger = LoggerFactory.getLogger(ClientWebServerImpl.class);

    private final DBClientService clientService;
    private final TemplateProcessor templateProcessor;
    private final Gson gson;
    private final Server server;

    public ClientWebServerImpl(DBClientService clientService,
                               int port,
                               TemplateProcessor templateProcessor, Gson gson) {
        this.clientService = clientService;
        this.server = new Server(port);
        this.templateProcessor = templateProcessor;
        this.gson = gson;
    }

    @Override
    public void start() {
        if (server.getHandlers().isEmpty()) {
            initContext();
        }
        try {
            server.start();
        } catch (Exception e) {
            logger.error("Server start failed", e);
            throw new ServerStartException(e);
        }
    }

    @Override
    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            logger.error("Server stop failed", e);
            throw new ServerStopException(e);
        }

    }

    @Override
    public void join() {
        try {
            server.join();
        } catch (Exception e) {
            logger.error("Server join failed", e);
            throw new ServerJoinException(e);
        }
    }

    private void initContext() {
        ResourceHandler resourceHandler = createResourceHandler("static", "index.html");
        ServletContextHandler servletContextHandler = createServletContextHandler();

        Handler.Sequence sequence = new Handler.Sequence();
        sequence.addHandler(resourceHandler);
        sequence.addHandler(applySecurity(servletContextHandler, "/api/user/*"));

        server.setHandler(sequence);
    }

    protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        return servletContextHandler;
    }

    private ResourceHandler createResourceHandler(String resourcesDirectory, String welcomePage) {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirAllowed(false);
        resourceHandler.setWelcomeFiles(welcomePage);
        resourceHandler.setBaseResourceAsString(
                FileSystemHelper.localFileNameOrResourceNameToFullPath(resourcesDirectory));
        return resourceHandler;
    }

    private ServletContextHandler createServletContextHandler() {
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.addServlet(new ServletHolder(new ClientServlet(clientService, gson)),
                "/api/client/*");
        return servletContextHandler;
    }
}
