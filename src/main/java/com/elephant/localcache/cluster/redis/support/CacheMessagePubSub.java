package com.elephant.localcache.cluster.redis.support;

import com.elephant.localcache.api.ClusterCache;
import com.elephant.localcache.cluster.CacheMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisCluster;

import java.util.List;


@Slf4j
public class CacheMessagePubSub extends ReliablePubSub {

    private List<ClusterCache> clusterCaches;
    private CacheMessageProcessor cacheMessageProcessor;


    public CacheMessagePubSub(JedisCluster jedisCluster,
                              List<ClusterCache> clusterCaches) {
        super(jedisCluster);
        this.clusterCaches = clusterCaches;
        this.cacheMessageProcessor = new CacheMessageProcessor(clusterCaches);
    }


    @SuppressWarnings("unchecked")
    @Override
    public void onMessage(String channel, String message) {
        //有消息redis就会立即发过来，所以这里要快速消费，如果耗时很长的需要listener做异步
        try {
            super.onMessage(channel, message);
            cacheMessageProcessor.process(message);
        } catch (Exception e) {
            log.error("redis channel process message exception,channel:{}, message:{}", channel, message, e);
        }
    }


}

