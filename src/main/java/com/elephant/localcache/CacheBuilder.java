package com.elephant.localcache;

import com.elephant.localcache.api.Cache;
import com.elephant.localcache.api.ClusterCache;
import com.elephant.localcache.caffeine.CaffeineCache;
import com.elephant.localcache.cluster.ClusterCacheConfig;
import com.elephant.localcache.cluster.DefaultClusterCache;

/**
 * @author : gejianhua
 * @date 2021/1/28 11:12
 */
public class CacheBuilder {


    public static <K, V> Cache<K, V> buildCache(CacheConfig<K, V> cacheConfig) {
        return new CaffeineCache<>(cacheConfig);
    }

    public static <K, V> ClusterCache<K, V> buildClusterCache(ClusterCacheConfig<K, V> clusterCacheConfig) {
        return new DefaultClusterCache<>(clusterCacheConfig, (AbstractCache<K, V>) buildCache(clusterCacheConfig));
    }

}
