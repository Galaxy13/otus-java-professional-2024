package ru.otus.cachehw;

import java.lang.ref.SoftReference;
import java.util.*;

public class MyCache<K, V> implements HwCache<K, V> {
    private final Map<K, SoftReference<V>> cache;
    private final List<HwListener<K, V>> listeners;
    // Надо реализовать эти методы

    private MyCache(int cacheSize) {
        this.cache = new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, SoftReference<V>> eldest) {
                if (size() > cacheSize) {
                    V value = Optional.ofNullable(eldest.getValue()).map(SoftReference::get).orElse(null);
                    notifyListeners(eldest.getKey(), value, "remove on overfill");
                    return true;
                }
                return false;
            }
        };
        this.listeners = new ArrayList<>();
    }

    public static <K, V> HwCache<K, V> create(int cacheSize) {
        if (cacheSize <= 0) {
            throw new IllegalArgumentException("Cache size must be greater than 0");
        }
        return new MyCache<>(cacheSize);
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, new SoftReference<>(value));
        this.listeners.forEach(listener -> listener.notify(key, value, "put"));
    }

    @Override
    public void remove(K key) {
        V removedValue;
        if (cache.containsKey(key)) {
            removedValue = cache.remove(key).get();
        } else {
            removedValue = null;
        }
        this.listeners.forEach(listener -> listener.notify(key, removedValue, "remove"));
    }

    @Override
    public Optional<V> get(K key) {
        if (cache.containsKey(key)) {
            V value = Optional.ofNullable(this.cache.get(key)).map(SoftReference::get).orElse(null);
            if (value == null) {
                this.cache.remove(key);
                notifyListeners(key, null, "remove null ref");
            }
            notifyListeners(key, value, "get");
            return Optional.ofNullable(value);
        } else {
            return Optional.empty();
        }
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
}
