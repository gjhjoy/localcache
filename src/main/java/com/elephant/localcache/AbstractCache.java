package com.elephant.localcache;

import com.elephant.localcache.api.Cache;
import com.elephant.localcache.api.CacheMessageListener;
import com.elephant.localcache.cluster.CacheMessage;
import com.elephant.localcache.support.LocalCacheException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;


/**
 * @author : gejianhua
 * @date 2021/1/26 10:58
 */
public abstract class AbstractCache<K, V> implements Cache<K, V>, CacheMessageListener {

    protected CacheConfig<K, V> cacheConfig;

    protected AbstractCache(CacheConfig<K, V> cacheConfig) {
        if (cacheConfig.getMaximumSize() <= 0) {
            throw new LocalCacheException("maximumSize must");
        }
        if (cacheConfig.getCacheLoader() == null) {
            throw new LocalCacheException("cacheLoader must");
        }
        if (cacheConfig.getRefreshAfterWrite() == null
                && cacheConfig.getExpireAfterWrite() == null
                && cacheConfig.getExpireAfterAccess() == null) {
            throw new LocalCacheException("refreshAfterWrite, expireAfterWrite, expireAfterAccess must one");
        }
        this.cacheConfig = cacheConfig.copy();
    }


    public abstract CacheValueHolder<V> doGet(K key);

    public abstract Map<K, CacheValueHolder<V>> doGetAll(List<K> keys);

    public abstract void doPut(K key, CacheValueHolder<V> cacheValueHolder);

    public abstract void doPutAll(Map<K, CacheValueHolder<V>> map);

    public abstract void doRemove(K key);

    public abstract void doRemoveAll(List<K> keys);

    @Override
    public void consumeMessage(CacheMessage cacheMessage) {

    }

    @Override
    public V get(K key) {
        return parseCacheValue(doGet(key));
    }

    @Override
    public List<V> getAll(List<K> keys) {
        return Lists.newArrayList(parseCacheValue(doGetAll(keys)).values());
    }

    @Override
    public void put(K key, V value) {
        if (value == null && !cacheConfig.isCacheNullValue()) {
            return;
        }
        doPut(key, CacheValueHolder.valueOf(value));
    }

    @Override
    public void putAll(Map<K, V> map) {
        if (MapUtils.isEmpty(map)) {
            return;
        }
        map.forEach((key, value) -> {
            put(key, value);
        });
    }

    @Override
    public void remove(K key) {
        doRemove(key);
    }

    @Override
    public void removeAll(List<K> keys) {
        doRemoveAll(keys);
    }


    protected V parseCacheValue(CacheValueHolder<V> cacheValueHolder) {
        if (cacheValueHolder == null) {
            return null;
        }
        return cacheValueHolder.getValue();
    }

    protected Map<K, V> parseCacheValue(Map<K, CacheValueHolder<V>> map) {
        if (MapUtils.isEmpty(map)) {
            return Maps.newHashMap();
        }
        Map<K, V> resultMap = Maps.newHashMap();
        map.forEach((key, holder) -> {
            V value = parseCacheValue(holder);
            if (value != null) {
                resultMap.put(key, value);
            }
        });

        return resultMap;
    }


}
