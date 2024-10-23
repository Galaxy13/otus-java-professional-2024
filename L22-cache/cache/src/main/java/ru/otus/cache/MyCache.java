package ru.otus.cache;

import java.util.*;

public class MyCache<K, V> implements HwCache<K, V> {

    private final Map<K, V> cache;
    private final List<HwListener<K, V>> listeners;

    private MyCache() {
        this.cache = new WeakHashMap<>();
        this.listeners = new ArrayList<>();
    }

    public static <K, V> HwCache<K, V> create() {
        return new MyCache<>();
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
        notifyListeners(key, value, "put");
    }

    @Override
    public void remove(K key) {
        V oldValue = cache.remove(key);
        if (oldValue != null) {
            notifyListeners(key, oldValue, "remove");
        }
    }

    @Override
    public Optional<V> get(K key) {
        V value = this.cache.get(key);
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
}
