package ru.otus.cache;

import org.junit.jupiter.api.Test;
import ru.otus.base.AbstractHibernateTest;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.HwListener;
import ru.otus.cachehw.MyCache;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.CacheProxy;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TestCache extends AbstractHibernateTest {
    private HwCache<Long, Client> testCache;

    @Test
    void testCacheBasicUsage() {
        this.testCache = MyCache.create(1);
        Map<String, Integer> opsCounter = new HashMap<>();

        HwListener<Long, Client> testListener = (key, value, op) -> opsCounter.merge(op, 1, Integer::sum);
        this.testCache.addListener(testListener);
        var dbCachedService = CacheProxy.wrapDbService(dbClientService, testCache);

        var savedClient = dbCachedService.saveClient(new Client("client1"));
        assertThat(opsCounter.get("put")).isEqualTo(1);

        dbCachedService.getById(savedClient.getId());
        assertThat(opsCounter.get("get")).isEqualTo(1);

        dbCachedService.saveClient(new Client("client2"));
        assertThat(opsCounter.get("clear")).isEqualTo(1);
        assertThat(opsCounter.get("overfill")).isEqualTo(1);
        assertThat(opsCounter.get("put")).isEqualTo(2);

        var client3 = dbClientService.saveClient(new Client("client3"));
        dbCachedService.getById(client3.getId());
        assertThat(opsCounter.get("get")).isEqualTo(2);
        assertThat(opsCounter.get("overfill")).isEqualTo(2);
        assertThat(opsCounter.get("clear")).isEqualTo(2);
        assertThat(opsCounter.get("put")).isEqualTo(3);
    }
}
