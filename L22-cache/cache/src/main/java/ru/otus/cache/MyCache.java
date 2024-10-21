package ru.otus.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.*;

public class MyCache<K, V> implements HwCache<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(MyCache.class);

    private final Map<K, SoftReference<V>> cache;
    private final Map<SoftReference<V>, K> valueMap;
    private final ReferenceQueue<V> queue;
    private final List<HwListener<K, V>> listeners;

    private MyCache(int cacheSize) {
        this.cache = createCacheMap(cacheSize);
        this.valueMap = createValueMap(cacheSize);
        this.listeners = new ArrayList<>();
        this.queue = new ReferenceQueue<>();
        Thread clearThread = createClearThread();
        clearThread.setDaemon(true);
        clearThread.start();
    }

    public static <K, V> HwCache<K, V> create(int cacheSize) {
        if (cacheSize <= 0) {
            throw new IllegalArgumentException("Cache size must be greater than 0");
        }
        return new MyCache<>(cacheSize);
    }

    @Override
    public void put(K key, V value) {
        var softRef = new SoftReference<>(value, queue);
        synchronized (cache) {
            cache.put(key, softRef);
            valueMap.put(softRef, key);
        }
        this.listeners.forEach(listener -> listener.notify(key, value, "put"));
    }

    @Override
    public void remove(K key) {
        Optional<SoftReference<V>> removedValue = Optional.ofNullable(cache.remove(key));
        if (removedValue.isPresent()) {
            SoftReference<V> softRef = removedValue.get();
            this.valueMap.remove(softRef);
            notifyListeners(key, softRef.get(), "remove");
        } else {
            notifyListeners(key, null, "emptyRemove");
        }
    }

    @Override
    public Optional<V> get(K key) {
        V value = Optional.ofNullable(this.cache.get(key)).map(SoftReference::get).orElse(null);
        notifyListeners(key, value, "get");
        return Optional.ofNullable(value);
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        this.listeners.add(Objects.requireNonNull(listener));
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        this.listeners.remove(listener);
    }

    private void notifyListeners(K key, V value, String action) {
        listeners.forEach(listener -> listener.notify(key, value, action));
    }

    private Map<K, SoftReference<V>> createCacheMap(int cacheSize) {
        return Collections.synchronizedMap(new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, SoftReference<V>> eldest) {
                if (size() > cacheSize) {
                    V value = eldest.getValue().get();
                    notifyListeners(eldest.getKey(), value, "overfill");
                    return true;
                }
                return false;
            }
        });
    }

    private Map<SoftReference<V>, K> createValueMap(int cacheSize) {
        return Collections.synchronizedMap(new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<SoftReference<V>, K> eldest) {
                return size() > cacheSize;
            }
        });
    }

    @SuppressWarnings("unchecked")
    private Thread createClearThread() {
        return new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    SoftReference<V> ref = (SoftReference<V>) queue.remove();
                    K key = valueMap.get(ref);
                    synchronized (cache) {
                        valueMap.remove(ref);
                        cache.remove(key);
                    }
                    notifyListeners(key, ref.get(), "gc");
                }
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while queue await. Cleaner stopped", e);
                Thread.currentThread().interrupt();
            }
        });
    }
}
