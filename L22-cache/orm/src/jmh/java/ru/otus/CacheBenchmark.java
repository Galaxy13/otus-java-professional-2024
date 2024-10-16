package ru.otus;

import org.hibernate.cfg.Configuration;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import ru.otus.base.TestContainersConfig;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.MyCache;
import ru.otus.core.repository.DataTemplate;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CacheBenchmark {

    private static final TestContainersConfig.CustomPostgreSQLContainer CONTAINER;

    static {
        CONTAINER = TestContainersConfig.CustomPostgreSQLContainer.getInstance();
        CONTAINER.start();
    }

    private DBClientService dbClientService;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CacheBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
        CONTAINER.stop();
    }

    @Benchmark
    @Measurement(timeUnit = TimeUnit.MILLISECONDS)
    @BenchmarkMode(Mode.SingleShotTime)
    public void measureCachedDbService() {
        HwCache<Long, Client> cache = MyCache.create(100);
        var cachedService = CacheProxy.wrapDbService(dbClientService, cache);

        fillAndExtract(cachedService);
    }

    @Benchmark
    @Measurement(timeUnit = TimeUnit.MILLISECONDS)
    @BenchmarkMode(Mode.SingleShotTime)
    public void measureDbService() {
        fillAndExtract(dbClientService);
    }

    private void fillAndExtract(DBClientService dbClientService) {
        List<Long> ids = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            ids.add(dbClientService.saveClient(new Client("name" + i)).getId());
        }
        for (Long id : ids) {
            dbClientService.getById(id);
        }
    }

    @Setup(Level.Trial)
    public void setupTrial() {
        String dbUrl = System.getProperty("app.datasource.demo-db.jdbcUrl");
        String dbUserName = System.getProperty("app.datasource.demo-db.username");
        String dbPassword = System.getProperty("app.datasource.demo-db.password");

        var migrationsExecutor = new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword);
        migrationsExecutor.executeMigrations();

        String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";
        Configuration configuration = new Configuration().configure(HIBERNATE_CFG_FILE);
        configuration.setProperty("hibernate.connection.url", dbUrl);
        configuration.setProperty("hibernate.connection.username", dbUserName);
        configuration.setProperty("hibernate.connection.password", dbPassword);

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        DataTemplate<Client> clientTemplate = new DataTemplateHibernate<>(Client.class);
        dbClientService = new DbClientServiceImpl(transactionManager, clientTemplate);
    }
}
