package com.AchadosPerdidos.API.Application.Interfaces.Cache;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ICacheService {

    <T> T getOrLoad(String cacheName, Object key, Supplier<T> valueLoader);

    <T> Optional<T> get(String cacheName, Object key);

    void put(String cacheName, Object key, Object value);

    void evict(String cacheName, Object key);

    void clear(String cacheName);

    void clearAll();

    boolean exists(String cacheName, Object key);

    String getCacheStats(String cacheName);

    void putAll(String cacheName, Map<Object, Object> entries);

    <T> Map<Object, T> getAll(String cacheName, Collection<Object> keys);

    void evictAll(String cacheName, Collection<Object> keys);

    <T> void warmUp(String cacheName, Collection<Object> keys, Function<Object, T> valueLoader);
}
