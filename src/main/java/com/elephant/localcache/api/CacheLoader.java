package com.elephant.localcache.api;

/**
 * @author : gejianhua
 * @date 2021/1/26 11:01
 */
public interface CacheLoader<K, V> {

    V load(K key);
}
