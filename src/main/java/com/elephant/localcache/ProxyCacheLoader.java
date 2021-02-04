package com.elephant.localcache;

import lombok.Getter;
import lombok.Setter;

/**
 * @author : gejianhua
 * @date 2021/1/26 11:41
 */
@Getter
@Setter
public class ProxyCacheLoader<K, V> {

    private CacheConfig<K, V> cacheConfig;

    public ProxyCacheLoader(CacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
    }

    public CacheValueHolder<V> load(K key) {
        V value = cacheConfig.getCacheLoader().load(key);
        if (value != null || cacheConfig.isCacheNullValue()) {
            return new CacheValueHolder<>(value);
        }
        return null;
    }

}
