package ru.otus;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.jdbc.mapper.DataTemplateJdbc;
import ru.otus.jdbc.mapper.core.repository.executor.DbExecutorImpl;
import ru.otus.jdbc.mapper.core.sessionmanager.TransactionRunnerJdbc;
import ru.otus.jdbc.mapper.crm.datasource.DriverManagerDataSource;
import ru.otus.jdbc.mapper.crm.model.Client;
import ru.otus.jdbc.mapper.crm.model.Manager;
import ru.otus.jdbc.mapper.crm.service.DbServiceClientImpl;
import ru.otus.jdbc.mapper.crm.service.DbServiceManagerImpl;

import javax.sql.DataSource;

@SuppressWarnings({"java:S125", "java:S1481", "java:S1854"})
public class HomeWork {
    private static final String URL = "jdbc:postgresql://localhost:5430/demoDB";
    private static final String USER = "usr";
    private static final String PASSWORD = "pwd";

    private static final Logger log = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {
        // Общая часть
        var dataSource = new DriverManagerDataSource(URL, USER, PASSWORD);
        flywayMigrations(dataSource);
        var transactionRunner = new TransactionRunnerJdbc(dataSource);
        var dbExecutor = new DbExecutorImpl();

        // Работа с клиентом
        var dataTemplateClient = new DataTemplateJdbc<>(
                dbExecutor, Client.class); // реализация DataTemplate, универсальная

        // Код дальше должен остаться
        var dbServiceClient = new DbServiceClientImpl(transactionRunner, dataTemplateClient);
        dbServiceClient.save(new Client("dbServiceFirst"));

        var clientSecond = dbServiceClient.save(new Client("dbServiceSecond"));
        var clientSecondSelected = dbServiceClient
                .getById(clientSecond.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecond.getId()));
        log.info("clientSecondSelected:{}", clientSecondSelected);

        // Сделайте тоже самое с классом Manager (для него надо сделать свою таблицу)

        var dataTemplateManager = new DataTemplateJdbc<>(dbExecutor, Manager.class);

        var dbServiceManager = new DbServiceManagerImpl(transactionRunner, dataTemplateManager);
        dbServiceManager.save(new Manager("ManagerFirst"));

        var managerSecond = dbServiceManager.save(new Manager("ManagerSecond"));
        var managerSecondSelected = dbServiceManager
                .getById(managerSecond.getNo())
                .orElseThrow(() -> new RuntimeException("Manager not found, id:" + managerSecond.getNo()));
        log.info("managerSecondSelected:{}", managerSecondSelected);
    }

    private static void flywayMigrations(DataSource dataSource) {
        log.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        log.info("db migration finished.");
        log.info("***");
    }
}
