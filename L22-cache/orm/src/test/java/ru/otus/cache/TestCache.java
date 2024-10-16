package ru.otus.cache;

import org.junit.jupiter.api.Test;
import ru.otus.base.AbstractHibernateTest;
import ru.otus.cachehw.MyCache;

public class TestCache extends AbstractHibernateTest {

    @Test
    void testCacheUsing() {
        var testCache = MyCache.create(10);
    }
}
