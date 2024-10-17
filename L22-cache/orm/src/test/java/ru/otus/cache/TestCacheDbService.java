package ru.otus.cache;

import org.junit.jupiter.api.Test;
import ru.otus.base.AbstractHibernateTest;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.CacheProxy;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TestCacheDbService extends AbstractHibernateTest {
    private HwCache<Long, Client> testCache;

    @Test
    void testCacheBasicUsage() {
        this.testCache = MyCache.create(1);
        Map<String, Integer> opsCounter = new HashMap<>();

        HwListener<Long, Client> testListener = (key, value, op) -> opsCounter.merge(op, 1, Integer::sum);
        this.testCache.addListener(testListener);
        var dbCachedService = CacheProxy.wrapDbService(dbClientService, testCache);

        var savedClient = dbCachedService.saveClient(new Client("client1"));
        assertThat(opsCounter).containsEntry("put", 1);

        dbCachedService.getById(savedClient.getId());
        assertThat(opsCounter).containsEntry("get", 1);

        dbCachedService.saveClient(new Client("client2"));
        assertThat(opsCounter).containsEntry("overfill", 1).containsEntry("put", 2);

        var client3 = dbClientService.saveClient(new Client("client3"));
        dbCachedService.getById(client3.getId());
        assertThat(opsCounter)
                .containsEntry("get", 2)
                .containsEntry("overfill", 2)
                .containsEntry("put", 3);
    }
}
