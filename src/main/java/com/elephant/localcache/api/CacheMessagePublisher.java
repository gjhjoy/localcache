package com.elephant.localcache.api;

import com.elephant.localcache.cluster.CacheMessage;

import java.util.List;

/**
 * @author : gejianhua
 * @date 2021/1/26 16:54
 */
public interface CacheMessagePublisher {

    /**
     * 启动
     */
    void start();

    /**
     * 销毁
     */
    void destory();

    /**
     * 发布消息
     * @param cacheMessage
     */
    boolean publish(CacheMessage cacheMessage);

    /**
     * 发布消息
     * @param clusterName
     * @param cacheName
     * @param keys
     * @return
     */
    default boolean publish(String clusterName, String cacheName, List<Object> keys){
        CacheMessage message = new CacheMessage();
        message.setKeyClassName(keys.get(0).getClass().getCanonicalName());
        message.setKeys(keys);
        message.setCacheName(cacheName);
        message.setClusterName(clusterName);
        return publish(message);
    }


}
