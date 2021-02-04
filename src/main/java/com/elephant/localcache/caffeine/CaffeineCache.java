package com.elephant.localcache.caffeine;

import com.elephant.localcache.AbstractCache;
import com.elephant.localcache.CacheConfig;
import com.elephant.localcache.CacheValueHolder;
import com.elephant.localcache.ProxyCacheLoader;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.apache.commons.collections4.MapUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author : gejianhua
 * @date 2021/1/26 10:58
 */
public class CaffeineCache<K, V> extends AbstractCache<K, V> {

    private LoadingCache<K, CacheValueHolder<V>> cache;
    private ProxyCacheLoader<K, V> proxyCacheLoader;


    public CaffeineCache(CacheConfig<K, V> cacheConfig) {
        super(cacheConfig);
        proxyCacheLoader = new ProxyCacheLoader<>(super.cacheConfig);
        this.cache = buildCache(super.cacheConfig);
    }


    @Override
    public CacheValueHolder<V> doGet(K key) {
        return cache.get(key);
    }

    @Override
    public Map<K, CacheValueHolder<V>> doGetAll(List<K> keys) {
        return cache.getAll(keys);
    }

    @Override
    public void doPut(K key, CacheValueHolder<V> cacheValueHolder) {
        if (cacheValueHolder != null) {
            cache.put(key, cacheValueHolder);
        }
    }

    @Override
    public void doPutAll(Map<K, CacheValueHolder<V>> map) {
        if (MapUtils.isEmpty(map)) {
            return;
        }
        cache.putAll(map);
    }

    @Override
    public void doRemove(K key) {
        cache.invalidate(key);
    }

    @Override
    public void doRemoveAll(List<K> keys) {
        cache.invalidateAll(keys);
    }


    @Override
    public String stats() {
        if (cacheConfig.isRecordStatus()) {
            CacheStats stats = cache.stats();
            return stats.toString() + ",hitRate=" + String.valueOf(stats.hitRate());
        } else {
            return super.toString();
        }
    }


    private LoadingCache<K, CacheValueHolder<V>> buildCache(CacheConfig cacheConfig) {
        Caffeine builder = Caffeine.newBuilder();
        builder.maximumSize(cacheConfig.getMaximumSize());

        if (cacheConfig.isRecordStatus()) {
            builder.recordStats();
        }
        if (cacheConfig.getExpireAfterAccess() != null) {
            builder.expireAfterAccess(cacheConfig.getExpireAfterAccess());
        }
        if (cacheConfig.getExpireAfterWrite() != null) {
            builder.expireAfterWrite(cacheConfig.getExpireAfterWrite());
        }

        if (cacheConfig.getRefreshAfterWrite() != null) {
            builder.refreshAfterWrite(cacheConfig.getRefreshAfterWrite());
        }


        return builder.build(new CacheLoader<K, CacheValueHolder<V>>() {
            @Nullable
            @Override
            public CacheValueHolder<V> load(@NonNull K key) throws Exception {
                return proxyCacheLoader.load(key);
            }
        });
    }
}
