package ru.otus;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.otus.jdbc.mapper.DataTemplateJdbc;
import ru.otus.jdbc.mapper.core.repository.executor.DbExecutor;
import ru.otus.jdbc.mapper.core.repository.executor.DbExecutorImpl;
import ru.otus.jdbc.mapper.core.sessionmanager.TransactionRunner;
import ru.otus.jdbc.mapper.core.sessionmanager.TransactionRunnerJdbc;
import ru.otus.jdbc.mapper.crm.datasource.DriverManagerDataSource;
import ru.otus.jdbc.mapper.crm.model.Client;
import ru.otus.jdbc.mapper.crm.model.Manager;
import ru.otus.jdbc.mapper.crm.service.DbServiceClientImpl;
import ru.otus.jdbc.mapper.crm.service.DbServiceManagerImpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Testcontainers
@SuppressWarnings("java:S125")
class OrmTest {

    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:13-alpine")
            .withDatabaseName("demoDB")
            .withUsername("usr")
            .withPassword("password")
            .withClasspathResourceMapping("table_creation.sql", "/docker-entrypoint-initdb.d/table_creation.sql", BindMode.READ_ONLY);
    private TransactionRunner transactionRunner;
    private DbExecutor dbExecutor;

    @BeforeAll
    static void startContainer() {
        container.start();
    }

    @AfterAll
    static void stopContainer() {
        container.stop();
        container.close();
    }

    @BeforeEach
    void setUp() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(container.getJdbcUrl(),
                container.getUsername(),
                container.getPassword());
        transactionRunner = new TransactionRunnerJdbc(dataSource);
        dbExecutor = new DbExecutorImpl();
    }

    @Test
    void testOrmOnClient() {
//        assertThat(false).isTrue();

        var dataTemplateClient = new DataTemplateJdbc<>(dbExecutor, Client.class);
        var dbServiceClient = new DbServiceClientImpl(transactionRunner, dataTemplateClient);

        var insertedClient1 = dbServiceClient.save(new Client("name1"));
        assertThat(insertedClient1).isNotNull();
        assertThat(insertedClient1.getName()).isEqualTo("name1");

        var getClient1 = dbServiceClient.getById(insertedClient1.getId());
        if (getClient1.isPresent()) {
            assertThat(getClient1.get().getName()).isEqualTo("name1");
            assertThat(getClient1.get().getId()).isEqualTo(insertedClient1.getId());
        } else {
            throw new RuntimeException("Result is null");
        }

        var insertedClient2 = dbServiceClient.save(new Client("name2"));
        var allClients = dbServiceClient.findAll();
        assertThat(allClients).hasSize(2);
        assertThat(allClients).satisfiesExactly(item1 -> {
                    assertThat(item1.getName()).isEqualTo("name1");
                    assertThat(item1.getId()).isEqualTo(insertedClient1.getId());
                },
                item2 -> {
                    assertThat(item2.getName()).isEqualTo("name2");
                    assertThat(item2.getId()).isEqualTo(insertedClient2.getId());
                });

        var updateClient = new Client(insertedClient1.getId(), "newName");
        dbServiceClient.save(updateClient);

        assertThat(dbServiceClient.findAll()).hasSize(2);
        var updatedClient1 = dbServiceClient.getById(updateClient.getId());
        if (updatedClient1.isPresent()) {
            assertThat(updatedClient1.get().getName()).isEqualTo("newName");
        } else {
            throw new RuntimeException("Update client failed. No client found with id");
        }
    }

    @Test
    void testOrmOnManager() {
        // assertThat(false).isTrue();

        var dataTemplateManager = new DataTemplateJdbc<>(dbExecutor, Manager.class);
        var dbServiceManager = new DbServiceManagerImpl(transactionRunner, dataTemplateManager);

        assertThat(dbServiceManager.getById(1)).isNotPresent();
        assertThat(dbServiceManager.findAll()).isEmpty();

        var insertedManager1 = dbServiceManager.save(new Manager("manager1"));
        var selectedManager1 = dbServiceManager.getById(insertedManager1.getNo());
        if (selectedManager1.isPresent()) {
            assertThat(selectedManager1.get().getLabel()).isEqualTo("manager1");
            assertThat(selectedManager1.get().getNo()).isEqualTo(insertedManager1.getNo());
            assertThat(selectedManager1.get().getParam1()).isNull();
        }

        var insertedManager2 = dbServiceManager.save(new Manager("manager2", "label2"));
        var selectedManager2 = dbServiceManager.getById(insertedManager2.getNo());
        if (selectedManager2.isPresent()) {
            assertThat(selectedManager2.get().getLabel()).isEqualTo("manager2");
            assertThat(selectedManager2.get().getNo()).isEqualTo(insertedManager2.getNo());
            assertThat(selectedManager2.get().getParam1()).isEqualTo("label2");
        }

        insertedManager2.setLabel("newLabel");
        insertedManager2.setParam1("updatedParam");
        dbServiceManager.save(insertedManager2);
        var updatedManager2 = dbServiceManager.getById(insertedManager2.getNo());
        if (updatedManager2.isPresent()) {
            assertThat(updatedManager2.get().getLabel()).isEqualTo("newLabel");
            assertThat(updatedManager2.get().getParam1()).isEqualTo("updatedParam");
        } else {
            throw new RuntimeException("Update manager failed. No obj found with no");
        }
    }
}
