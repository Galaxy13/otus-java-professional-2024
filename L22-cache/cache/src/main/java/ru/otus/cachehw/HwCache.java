package ru.otus.cachehw;

import java.util.Optional;

public interface HwCache<K, V> {

    void put(K key, V value);

    void remove(K key);

    Optional<V> get(K key);

    void addListener(HwListener<K, V> listener);

    void removeListener(HwListener<K, V> listener);
}
