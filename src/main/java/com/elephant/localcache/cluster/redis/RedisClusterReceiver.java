package com.elephant.localcache.cluster.redis;

import com.elephant.localcache.api.CacheMessageReceiver;
import com.elephant.localcache.api.ClusterCache;
import com.elephant.localcache.cluster.redis.support.CacheMessagePubSub;
import com.elephant.localcache.cluster.redis.support.RedisManager;
import com.elephant.localcache.support.LocalCacheException;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.Set;

/**
 * @author : gejianhua
 * @date 2021/1/27 11:05
 */
@Slf4j
public class RedisClusterReceiver implements CacheMessageReceiver {

    private List<ClusterCache> clusterCaches;
    private RedisManager redisManager;
    private JedisCluster jedisCluster;
    private CacheMessagePubSub pubsub;

    public RedisClusterReceiver(List<ClusterCache> clusterCaches) {
        if (CollectionUtils.isEmpty(clusterCaches)) {
            throw new LocalCacheException("cluster cache is empty");
        }
        this.clusterCaches = clusterCaches;
        this.redisManager = new RedisManager();
    }

    @Override
    public void start() {
        jedisCluster = redisManager.getJedisCluster();
        if (jedisCluster == null) {
            throw new LocalCacheException("jedis cluster is null");
        }
        pubsub = new CacheMessagePubSub(jedisCluster, clusterCaches);
        pubsub.subscribe(channel());
    }

    @Override
    public void destory() {
        if (pubsub != null) {
            try {
                pubsub.unsubscribe();
            } catch (Exception e) {
                log.error("redis pubsub destory and unsubscribe exception", e);
            }
        }
    }


    private String[] channel() {
        Set<String> channelSet = Sets.newHashSet();
        clusterCaches.forEach(clusterCache -> {
            channelSet.add(redisManager.channel(clusterCache.clusterName()));
        });
        String[] channels = new String[channelSet.size()];
        return channelSet.toArray(channels);
    }
}
