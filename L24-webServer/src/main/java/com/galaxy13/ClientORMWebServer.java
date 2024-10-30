package com.galaxy13;

import com.galaxy13.helpers.FileSystemHelper;
import com.galaxy13.orm.ORMInitialize;
import com.galaxy13.processor.TemplateProcessor;
import com.galaxy13.processor.TemplateProcessorImpl;
import com.galaxy13.server.ClientWebServer;
import com.galaxy13.server.ClientWebServerImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.cli.*;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.util.resource.PathResourceFactory;
import org.eclipse.jetty.util.resource.Resource;
import ru.otus.crm.service.DBClientService;

import java.net.URI;

public class ClientORMWebServer {
    private static final String TEMPLATES_DIR = "/templates/";
    private static final String HASH_LOGIN_SERVICE_CONFIG_NAME = "realm.properties";
    private static final String REALM_NAME = "AnyRealm";

    public static void main(String[] args) throws ParseException {
        int port = getPort(args);
        DBClientService clientService = ORMInitialize.initializeHibernate("hibernate.cfg.xml");
        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        LoginService loginService = new HashLoginService(REALM_NAME,
                getResource());
        ClientWebServer server = new ClientWebServerImpl(clientService,
                port,
                templateProcessor,
                gson, loginService);
        server.start();
    }

    private static int getPort(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption(Option.builder("p")
                .longOpt("port")
                .hasArg()
                .desc("Listen port of Jetty Server")
                .required(false)
                .build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        return Integer.parseInt(cmd.getOptionValue("p", "27015"));
    }

    private static Resource getResource() {
        String hashLoginServiceConfigPath =
                FileSystemHelper.localFileNameOrResourceNameToFullPath(HASH_LOGIN_SERVICE_CONFIG_NAME);
        PathResourceFactory pathResourceFactory = new PathResourceFactory();
        return pathResourceFactory.newResource(URI.create(hashLoginServiceConfigPath));
    }
}
