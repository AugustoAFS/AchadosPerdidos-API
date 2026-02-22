package com.AchadosPerdidos.API.Application.Services.Cache;

import com.AchadosPerdidos.API.Application.Interfaces.Cache.ICacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class CacheService implements ICacheService {

    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);

    @Autowired
    private CacheManager cacheManager;

    @Override
    public <T> T getOrLoad(String cacheName, Object key, Supplier<T> valueLoader) {
        Cache cache = getCache(cacheName);
        if (cache == null) {
            logger.warn("Cache '{}' não encontrado, executando valueLoader diretamente", cacheName);
            return valueLoader.get();
        }

        Cache.ValueWrapper wrapper = cache.get(key);
        if (wrapper != null) {
            @SuppressWarnings("unchecked")
            T cachedValue = (T) wrapper.get();
            logger.debug("✅ Cache HIT  | cache='{}' | key='{}'", cacheName, key);
            return cachedValue;
        }

        logger.debug("❌ Cache MISS | cache='{}' | key='{}' — carregando da fonte", cacheName, key);
        T value = valueLoader.get();

        if (value != null) {
            cache.put(key, value);
            logger.debug("💾 Cache PUT  | cache='{}' | key='{}'", cacheName, key);
        }

        return value;
    }

    @Override
    public <T> Optional<T> get(String cacheName, Object key) {
        Cache cache = getCache(cacheName);
        if (cache == null) {
            return Optional.empty();
        }

        Cache.ValueWrapper wrapper = cache.get(key);
        if (wrapper != null) {
            @SuppressWarnings("unchecked")
            T value = (T) wrapper.get();
            logger.debug("✅ Cache HIT  | cache='{}' | key='{}'", cacheName, key);
            return Optional.ofNullable(value);
        }

        logger.debug("❌ Cache MISS | cache='{}' | key='{}'", cacheName, key);
        return Optional.empty();
    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        Cache cache = getCache(cacheName);
        if (cache != null && value != null) {
            cache.put(key, value);
            logger.debug("💾 Cache PUT  | cache='{}' | key='{}'", cacheName, key);
        }
    }

    @Override
    public void evict(String cacheName, Object key) {
        Cache cache = getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
            logger.debug("🗑️ Cache EVICT | cache='{}' | key='{}'", cacheName, key);
        }
    }

    @Override
    public void clear(String cacheName) {
        Cache cache = getCache(cacheName);
        if (cache != null) {
            cache.clear();
            logger.info("🗑️ Cache CLEAR | cache='{}' — todas as entradas removidas", cacheName);
        }
    }

    @Override
    public void clearAll() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
        logger.info("🗑️ Cache CLEAR ALL — todos os caches foram limpos");
    }

    @Override
    public boolean exists(String cacheName, Object key) {
        Cache cache = getCache(cacheName);
        return cache != null && cache.get(key) != null;
    }

    @Override
    public String getCacheStats(String cacheName) {
        Cache cache = getCache(cacheName);
        if (cache instanceof org.springframework.cache.caffeine.CaffeineCache caffeineCache) {
            var nativeCache = caffeineCache.getNativeCache();
            var stats = nativeCache.stats();

            return String.format(
                    "Cache '%s' [Caffeine] | hitRate=%.2f%% | hits=%d | misses=%d | evictions=%d | tamanho≈%d",
                    cacheName,
                    stats.hitRate() * 100,
                    stats.hitCount(),
                    stats.missCount(),
                    stats.evictionCount(),
                    nativeCache.estimatedSize());
        }
        return "Estatísticas não disponíveis para o cache: " + cacheName;
    }

    @Override
    public void putAll(String cacheName, Map<Object, Object> entries) {
        if (entries == null || entries.isEmpty()) {
            return;
        }
        Cache cache = getCache(cacheName);
        if (cache == null) {
            return;
        }

        int stored = 0;
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            if (entry.getValue() != null) {
                cache.put(entry.getKey(), entry.getValue());
                stored++;
            }
        }
        logger.info("💾 Cache PUT-ALL | cache='{}' | {} entradas armazenadas em lote", cacheName, stored);
    }

    @Override
    public <T> Map<Object, T> getAll(String cacheName, Collection<Object> keys) {
        Map<Object, T> result = new HashMap<>();
        if (keys == null || keys.isEmpty()) {
            return result;
        }

        Cache cache = getCache(cacheName);
        if (cache == null) {
            return result;
        }

        int hits = 0;
        for (Object key : keys) {
            Cache.ValueWrapper wrapper = cache.get(key);
            if (wrapper != null) {
                @SuppressWarnings("unchecked")
                T value = (T) wrapper.get();
                if (value != null) {
                    result.put(key, value);
                    hits++;
                }
            }
        }

        logger.debug("✅ Cache GET-ALL | cache='{}' | {}/{} chaves encontradas",
                cacheName, hits, keys.size());
        return result;
    }

    @Override
    public void evictAll(String cacheName, Collection<Object> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        Cache cache = getCache(cacheName);
        if (cache == null) {
            return;
        }

        keys.forEach(cache::evict);
        logger.info("🗑️ Cache EVICT-ALL | cache='{}' | {} chaves removidas em lote", cacheName, keys.size());
    }

    @Override
    public <T> void warmUp(String cacheName, Collection<Object> keys, Function<Object, T> valueLoader) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        Cache cache = getCache(cacheName);
        if (cache == null) {
            logger.warn("⚠️ WarmUp ignorado — cache '{}' não encontrado", cacheName);
            return;
        }

        logger.info("🔥 Cache WARM-UP | cache='{}' | iniciando pré-carga de {} chaves", cacheName, keys.size());
        int loaded = 0;
        for (Object key : keys) {
            try {
                T value = valueLoader.apply(key);
                if (value != null) {
                    cache.put(key, value);
                    loaded++;
                }
            } catch (Exception e) {
                logger.warn("⚠️ WarmUp falhou para key='{}' no cache='{}': {}", key, cacheName, e.getMessage());
            }
        }
        logger.info("🔥 Cache WARM-UP | cache='{}' | {}/{} entradas carregadas com sucesso",
                cacheName, loaded, keys.size());
    }

    private Cache getCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            logger.warn("⚠️ Cache '{}' não encontrado no CacheManager", cacheName);
        }
        return cache;
    }
}
