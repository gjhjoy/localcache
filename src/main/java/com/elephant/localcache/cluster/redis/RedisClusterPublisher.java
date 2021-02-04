package com.elephant.localcache.cluster.redis;

import com.alibaba.fastjson.JSON;
import com.elephant.localcache.api.CacheMessagePublisher;
import com.elephant.localcache.cluster.CacheMessage;
import com.elephant.localcache.cluster.redis.support.RedisManager;
import com.elephant.localcache.support.ClusterKeyTypeUtils;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.Set;

/**
 * @author : gejianhua
 * @date 2021/1/27 11:05
 */
@Slf4j
public class RedisClusterPublisher implements CacheMessagePublisher {

    private Set<HostAndPort> hostAndPorts;
    private String password;
    private JedisCluster jedisCluster;
    private RedisManager redisManager;

    public RedisClusterPublisher(Set<HostAndPort> hostAndPorts, String password) {
        this.hostAndPorts = hostAndPorts;
        this.password = password;
        redisManager = new RedisManager();
    }


    @Override
    public void start() {
        log.info("正在创建jedis cluster实例");
        jedisCluster = redisManager.getAndInitJedisCluster(hostAndPorts, password);
    }

    @Override
    public void destory() {
        jedisCluster.close();
    }

    @Override
    public boolean publish(CacheMessage cacheMessage) {
        int times = 3;
        for (int i = 0; i < times; i++) {
            try {
                log.info("start publish cache message:{}", cacheMessage);
                ClusterKeyTypeUtils.checkTypeSupport(cacheMessage.getKeys());
                jedisCluster.publish(redisManager.channel(cacheMessage.getClusterName()), JSON.toJSONString(cacheMessage));
                return true;
            } catch (Exception e) {
                log.error("redis cluster cache publish message exception, {}", cacheMessage, e);
            }
        }

        return false;
    }

    @Override
    public boolean publish(String clusterName, String cacheName, List<Object> keys) {
        CacheMessage message = new CacheMessage();
        message.setKeyClassName(keys.get(0).getClass().getCanonicalName());
        message.setKeys(keys);
        message.setCacheName(cacheName);
        message.setClusterName(clusterName);
        return publish(message);
    }


}
