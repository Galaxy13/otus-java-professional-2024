package com.galaxy13.server;

import com.galaxy13.helpers.FileSystemHelper;
import com.galaxy13.processor.TemplateProcessor;
import com.galaxy13.server.exception.ServerStartException;
import com.galaxy13.server.exception.ServerStopException;
import com.galaxy13.servlet.AdminPageServlet;
import com.galaxy13.servlet.ClientApiServlet;
import com.google.gson.Gson;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.ee10.servlet.security.ConstraintMapping;
import org.eclipse.jetty.ee10.servlet.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.Constraint;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import ru.otus.crm.service.DBClientService;

import java.util.ArrayList;
import java.util.List;

public class ClientWebServerImpl implements ClientWebServer {
    private static final String ADMIN_ROLE_NAME = "admin";

    private final DBClientService clientService;
    private final TemplateProcessor templateProcessor;
    private final Gson gson;
    private final Server server;
    private final LoginService loginService;

    public ClientWebServerImpl(DBClientService clientService,
                               int port,
                               TemplateProcessor templateProcessor,
                               Gson gson, LoginService loginService) {
        this.clientService = clientService;
        this.server = new Server(port);
        this.templateProcessor = templateProcessor;
        this.gson = gson;
        this.loginService = loginService;
    }

    @Override
    public void start() {
        if (server.getHandlers().isEmpty()) {
            initContext();
        }
        try {
            server.start();
        } catch (Exception e) {
            throw new ServerStartException(e);
        }
    }

    @Override
    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new ServerStopException(e);
        }

    }

    @Override
    public void join() throws InterruptedException {
        server.join();
    }

    private void initContext() {
        ResourceHandler resourceHandler = createResourceHandler();
        ServletContextHandler servletContextHandler = createServletContextHandler();

        Handler.Sequence sequence = new Handler.Sequence();
        sequence.addHandler(resourceHandler);
        sequence.addHandler(applySecurity(servletContextHandler, "/api/client/*", "/clients/"));

        server.setHandler(sequence);
    }

    protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        Constraint constraint = Constraint.from(ADMIN_ROLE_NAME);

        List<ConstraintMapping> constraintMappings = new ArrayList<>();
        for (String path : paths) {
            ConstraintMapping mapping = new ConstraintMapping();
            mapping.setPathSpec(path);
            mapping.setConstraint(constraint);
            constraintMappings.add(mapping);
        }

        ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
        securityHandler.setAuthenticator(new BasicAuthenticator());
        securityHandler.setLoginService(loginService);
        securityHandler.setConstraintMappings(constraintMappings);
        securityHandler.setHandler(new Handler.Wrapper(servletContextHandler));
        return securityHandler;
    }

    private ResourceHandler createResourceHandler() {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirAllowed(false);
        resourceHandler.setWelcomeFiles("index.html");
        resourceHandler.setBaseResourceAsString(
                FileSystemHelper.localFileNameOrResourceNameToFullPath("static"));
        return resourceHandler;
    }

    private ServletContextHandler createServletContextHandler() {
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.addServlet(new ServletHolder(new ClientApiServlet(clientService, gson)),
                "/api/client/*");
        servletContextHandler.addServlet(new ServletHolder(new AdminPageServlet(templateProcessor, clientService)),
                "/clients/");
        return servletContextHandler;
    }
}
