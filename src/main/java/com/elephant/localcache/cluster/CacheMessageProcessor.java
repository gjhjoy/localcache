package com.elephant.localcache.cluster;

import com.alibaba.fastjson.JSON;
import com.elephant.localcache.AbstractCache;
import com.elephant.localcache.api.ClusterCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author : gejianhua
 * @date 2021/1/27 10:07
 */
@Slf4j
public class CacheMessageProcessor {

    private List<ClusterCache> clusterCaches;

    public CacheMessageProcessor(List<ClusterCache> clusterCaches) {
        this.clusterCaches = clusterCaches;
    }


    public void process(String message) {
        try {
            log.info("start process cache message:{}", message);
            CacheMessage cacheMessage = JSON.parseObject(message, CacheMessage.class);
            if (CollectionUtils.isEmpty(cacheMessage.getKeys())) {
                return;
            }

            ClusterCache clusterCache = clusterCaches.stream()
                    .filter(x -> x.clusterName().equals(cacheMessage.getClusterName()))
                    .filter(x -> x.cacheName().equals(cacheMessage.getCacheName()))
                    .findFirst().orElse(null);

            if (clusterCache != null) {
                AbstractCache abstractCache = (AbstractCache) clusterCache;
                abstractCache.consumeMessage(cacheMessage);
            } else {
                log.error("process cache message failed, cluster cache not found, message:{}", message);
            }
        } catch (Exception e) {
            log.error("process cache message exception, message:{}", message, e);
        }
    }


}
