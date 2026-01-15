package com.AchadosPerdidos.API.Application.Config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;


@Configuration
@EnableCaching
public class CacheConfig {

    private static final List<String> CACHE_NAMES = List.of(
            "itens",
            "usuarios",
            "campus",
            "instituicoes",
            "cidades",
            "estados",
            "enderecos",
            "roles",
            "fotos",
            "deviceTokens",
            "jwtTokens"
    );

    @Value("${cache.max-size}")
    private int MaxSize;

    @Value("${cache.expire-after-write-minutes}")
    private int ExpireAfterWrite;

    @Value("${cache.expire-after-access-minutes}")
    private int ExpireAfterAccess;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCacheNames(CACHE_NAMES);
        manager.setAllowNullValues(false);
        manager.setCaffeine(caffeineBuilder());
        return manager;
    }

    @Bean
    public Caffeine<Object, Object> caffeineBuilder() {
        return Caffeine.newBuilder()
                .maximumSize(MaxSize)
                .expireAfterWrite(Duration.ofMinutes(ExpireAfterWrite))
                .expireAfterAccess(Duration.ofMinutes(ExpireAfterAccess));
    }
}