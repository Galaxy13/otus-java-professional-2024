package ru.otus.cachehw;

import java.lang.ref.SoftReference;
import java.util.*;

public class MyCache<K, V> implements HwCache<K, V> {
    private final Map<K, SoftReference<V>> cache;
    private final List<HwListener<K, V>> listeners;
    private final int cacheSize;
    // Надо реализовать эти методы

    private MyCache(int cacheSize) {
        this.cacheSize = cacheSize;
        this.cache = new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, SoftReference<V>> eldest) {
                if (size() > cacheSize) {
                    V value = eldest.getValue().get();
                    notifyListeners(eldest.getKey(), value, "overfill");
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
        if (this.cacheSize == cache.size()) {
            this.clearCache();
        }
        cache.put(key, new SoftReference<>(value));
        this.listeners.forEach(listener -> listener.notify(key, value, "put"));
    }

    @Override
    public void remove(K key) {
        V removedValue = Optional.ofNullable(cache.remove(key)).map(SoftReference::get).orElse(null);
        if (removedValue != null) {
            notifyListeners(key, removedValue, "remove");
        } else {
            notifyListeners(key, null, "nothing to remove");
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

    private void clearCache() {
        this.cache.entrySet().removeIf(entry -> entry.getValue().get() == null);
        notifyListeners(null, null, "clear");
    }
}
