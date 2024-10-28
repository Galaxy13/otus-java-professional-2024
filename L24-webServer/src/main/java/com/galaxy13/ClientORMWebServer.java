package com.galaxy13;

import com.galaxy13.orm.ORMInitialize;
import com.galaxy13.processor.TemplateProcessor;
import com.galaxy13.processor.TemplateProcessorImpl;
import com.galaxy13.server.ClientWebServer;
import com.galaxy13.server.ClientWebServerImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.cli.*;
import ru.otus.crm.service.DBClientService;

public class ClientORMWebServer {
    private static String TEMPLATES_DIR = "/templates/";

    public static void main(String[] args) {
        int port = getPort(args);
        DBClientService clientService = ORMInitialize.initializeHibernate("hibernate.cfg.xml");
        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        ClientWebServer server = new ClientWebServerImpl(clientService, port, templateProcessor, gson);
        server.start();
    }

    private static int getPort(String[] args) {
        Options options = new Options();
        options.addOption(Option.builder("p")
                .longOpt("port")
                .hasArg()
                .desc("Listen port of Jetty Server")
                .required(false)
                .build());

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            return Integer.parseInt(cmd.getOptionValue("p", "27015"));
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse port number", e);
        }
    }
}
