package com.elephant.localcache.api;

import java.util.List;
import java.util.Map;

/**
 * @author : gejianhua
 * @date 2021/1/26 10:53
 */
public interface Cache<K, V> {


    /**
     * 获取数据，如果不存在会从CacheLoader中加载
     * @param key
     * @return
     */
    V get(K key);

    /**
     * 批量获取数据，如果不存在会从CacheLoader中加载
     * @param keys
     * @return
     */
    List<V> getAll(List<K> keys);

    /**
     * 加入缓存
     * @param key
     * @param value
     */
    void put(K key, V value);

    /**
     * 批量加入缓存
     * @param map
     */
    void putAll(Map<K, V> map);

    /**
     * 移除缓存
     * @param key
     */
    void remove(K key);

    /**
     * 批量移除缓存
     * @param keys
     */
    void removeAll(List<K> keys);

    /**
     * 状态
     * @return
     */
    String stats();


}
