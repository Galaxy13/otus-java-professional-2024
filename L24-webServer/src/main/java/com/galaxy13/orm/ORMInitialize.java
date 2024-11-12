package com.galaxy13.orm;

import org.hibernate.cfg.Configuration;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.CacheProxy;
import ru.otus.crm.service.DBClientService;
import ru.otus.crm.service.DbClientServiceImpl;

public class ORMInitialize {
    private ORMInitialize() {
        throw new UnsupportedOperationException("Utility class. Initializing prohibited");
    }

    public static DBClientService initializeHibernate(String hibernateConfigFile) {
        var configuration = new Configuration().configure(hibernateConfigFile);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var username = configuration.getProperty("hibernate.connection.username");
        var password = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, username, password).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration,
                Client.class,
                Address.class,
                Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        var dbService = new DbClientServiceImpl(transactionManager, clientTemplate);
        return CacheProxy.wrap(dbService);
    }
}
