package com.elephant.localcache.cluster;

import com.elephant.localcache.CacheConfig;
import com.elephant.localcache.api.CacheMessagePublisher;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : gejianhua
 * @date 2021/1/26 11:08
 */
@Getter
@Setter
public class ClusterCacheConfig<K, V> extends CacheConfig<K, V> {

    /**
     * 集群名称，一般使用项目名称，比如udc
     * 如果使用redis，cluster name作为channel name
     * 如果使用rocketmq, cluster name作为 topic name
     */
    private String clusterName;
    /**
     * 缓存名称，clusterName + cacheName 需要惟一
     */
    private String cacheName;

    /**
     * 消息发布器
     */
    private CacheMessagePublisher cacheMessagePublisher;


    @Override
    public ClusterCacheConfig<K, V> copy() {
        ClusterCacheConfig<K, V> config = new ClusterCacheConfig<>();
        config.setCacheNullValue(isCacheNullValue());
        config.setMaximumSize(getMaximumSize());
        config.setRefreshAfterWrite(getRefreshAfterWrite());
        config.setRecordStatus(isRecordStatus());
        config.setCacheLoader(getCacheLoader());
        config.setExpireAfterWrite(getExpireAfterWrite());
        config.setExpireAfterAccess(getExpireAfterAccess());
        config.setClusterName(clusterName);
        config.setCacheName(cacheName);
        config.setCacheMessagePublisher(cacheMessagePublisher);
        return config;
    }

}
