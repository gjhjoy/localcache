package com.elephant.localcache.cluster.redis.support;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;

/**
 * @author : gejianhua
 * @date 2021/1/27 11:06
 */
public class RedisManager {

    private static final String Cluster_Name_Prefix_Redis = "clu:cac:xx:";

    private static JedisCluster jedisCluster;

    public JedisCluster getAndInitJedisCluster(Set<HostAndPort> hostAndPorts, String password) {
        if(jedisCluster != null){
            return jedisCluster;
        }
        synchronized (RedisManager.class) {
            if (jedisCluster != null) {
                return jedisCluster;
            }

            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMinIdle(2);
            poolConfig.setMaxTotal(2);
            poolConfig.setMaxIdle(2);

            jedisCluster = new JedisCluster(hostAndPorts, 60000, 5000, 3, password, poolConfig);
            return jedisCluster;
        }
    }

    public JedisCluster getJedisCluster() {
        return jedisCluster;
    }



    public String channel(String clusterName) {
        return Cluster_Name_Prefix_Redis + clusterName;
    }
}
