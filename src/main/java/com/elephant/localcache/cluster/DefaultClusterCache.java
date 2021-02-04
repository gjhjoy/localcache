package com.elephant.localcache.cluster;

import com.elephant.localcache.AbstractCache;
import com.elephant.localcache.CacheValueHolder;
import com.elephant.localcache.api.ClusterCache;
import com.elephant.localcache.support.ClusterKeyTypeUtils;
import com.elephant.localcache.support.LocalCacheException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author : gejianhua
 * @date 2021/1/26 11:15
 */
@Slf4j
public class DefaultClusterCache<K, V> extends AbstractCache<K, V> implements ClusterCache<K, V> {

    private AbstractCache<K, V> localCache;
    private ClusterCacheConfig clusterCacheConfig;

    private AtomicLong publishSuccessCount = new AtomicLong(0L);
    private AtomicLong publishFailedCount = new AtomicLong(0L);

    public DefaultClusterCache(ClusterCacheConfig<K, V> clusterCacheConfig, AbstractCache<K, V> localCache) {
        super(clusterCacheConfig);

        if (StringUtils.isBlank(clusterCacheConfig.getClusterName())) {
            throw new LocalCacheException("cluster name must for cluster cache!");
        }
        if (StringUtils.isBlank(clusterCacheConfig.getCacheName())) {
            throw new LocalCacheException("cache name must for cluster cache!");
        }
        if (clusterCacheConfig.getCacheMessagePublisher() == null) {
            throw new LocalCacheException("cache message publisher must for cluster cache!");
        }
        this.clusterCacheConfig = clusterCacheConfig.copy();
        this.localCache = localCache;
    }

    @Override
    public CacheValueHolder<V> doGet(K key) {
        ClusterKeyTypeUtils.checkTypeSupport(key);
        return localCache.doGet(key);
    }

    @Override
    public Map<K, CacheValueHolder<V>> doGetAll(List<K> keys) {
        ClusterKeyTypeUtils.checkTypeSupport(keys);
        return localCache.doGetAll(keys);
    }

    @Override
    public void doPut(K key, CacheValueHolder<V> cacheValueHolder) {
        ClusterKeyTypeUtils.checkTypeSupport(key);
        localCache.doPut(key, cacheValueHolder);
        publishMessage(Lists.newArrayList(key));
    }

    @Override
    public void doPutAll(Map<K, CacheValueHolder<V>> map) {
        ClusterKeyTypeUtils.checkTypeSupport(map.keySet());
        localCache.doPutAll(map);

        if (MapUtils.isEmpty(map)) {
            return;
        }
        publishMessage(Lists.newArrayList(map.keySet()));
    }

    @Override
    public void doRemove(K key) {
        ClusterKeyTypeUtils.checkTypeSupport(key);
        localCache.doRemove(key);
        publishMessage(Lists.newArrayList(key));
    }

    @Override
    public void doRemoveAll(List<K> keys) {
        ClusterKeyTypeUtils.checkTypeSupport(keys);
        localCache.doRemoveAll(keys);

        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        publishMessage(keys);
    }

    @Override
    public void consumeMessage(CacheMessage cacheMessage) {
        super.consumeMessage(cacheMessage);
        if (cacheMessage == null || CollectionUtils.isEmpty(cacheMessage.getKeys())) {
            return;
        }
        cacheMessage.getKeys().forEach(key -> {
            try {
                localCache.doRemove((K) ClusterKeyTypeUtils.getRealTypeKey(key, cacheMessage.getKeyClassName()));
            } catch (Exception e) {
                log.error("consume cache message exception, message:{}, key:{}", cacheMessage, key, e);
            }
        });
    }

    @Override
    public String stats() {
        return localCache.stats()
                + ",publishSuccessCount=" + publishSuccessCount.get()
                + ",publishFailedCount=" + publishFailedCount.get();
    }


    @Override
    public String clusterName() {
        return clusterCacheConfig.getClusterName();
    }

    @Override
    public String cacheName() {
        return clusterCacheConfig.getCacheName();
    }


    private void publishMessage(List<K> keys) {
        try {
            CacheMessage cacheMessage = new CacheMessage();
            cacheMessage.setCacheName(cacheName());
            cacheMessage.setClusterName(clusterName());
            cacheMessage.setKeys(Lists.newArrayList(keys));
            cacheMessage.setKeyClassName(keys.get(0).getClass().getCanonicalName());
            boolean success = clusterCacheConfig.getCacheMessagePublisher().publish(cacheMessage);

            if (clusterCacheConfig.isRecordStatus()) {
                if (success) {
                    publishSuccessCount.incrementAndGet();
                } else {
                    publishFailedCount.incrementAndGet();
                }
            }
        } catch (Exception e) {
            log.error("publish cache message exception, cluster name:{}, cache name:{}", clusterName(), cacheName(), e);
            if (clusterCacheConfig.isRecordStatus()) {
                publishFailedCount.incrementAndGet();
            }
        }
    }
}
