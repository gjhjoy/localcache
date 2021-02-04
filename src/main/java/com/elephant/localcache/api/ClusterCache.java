package com.elephant.localcache.api;

/**
 * @author : gejianhua
 * @date 2021/1/26 10:53
 */
public interface ClusterCache<K, V> extends Cache<K, V> {

    /**
     * 集群名称
     * @return
     */
    String clusterName();

    /**
     * 缓存名称
     * @return
     */
    String cacheName();


}
