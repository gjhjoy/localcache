package com.elephant.localcache.test;

import com.elephant.localcache.CacheBuilder;
import com.elephant.localcache.api.CacheLoader;
import com.elephant.localcache.api.CacheMessagePublisher;
import com.elephant.localcache.api.CacheMessageReceiver;
import com.elephant.localcache.api.ClusterCache;
import com.elephant.localcache.cluster.ClusterCacheConfig;
import com.elephant.localcache.cluster.redis.RedisClusterPublisher;
import com.elephant.localcache.cluster.redis.RedisClusterReceiver;
import com.elephant.localcache.support.Threads;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * @author : gejianhua
 * @date 2021/1/29 20:05
 */
@Slf4j
public class RedisClusterCacheTest extends BaseTest {

    private static ClusterCache<Long, Long> clusterCache;
    private static ClusterCache<Long, Long> clusterCache2;
    private static CacheMessagePublisher cacheMessagePublisher;
    private static CacheMessageReceiver cacheMessageReceiver;
    private static int loadCount = 0;



    @BeforeClass
    public static void init() {
        Set<HostAndPort> hostAndPortsSet = new HashSet<>();
        // 添加节点
        hostAndPortsSet.add(new HostAndPort("192.168.27.11", 8001));
        hostAndPortsSet.add(new HostAndPort("192.168.27.11", 8002));
        hostAndPortsSet.add(new HostAndPort("192.168.27.12", 8001));
        hostAndPortsSet.add(new HostAndPort("192.168.27.12", 8002));
        hostAndPortsSet.add(new HostAndPort("192.168.27.13", 8001));
        hostAndPortsSet.add(new HostAndPort("192.168.27.13", 8002));

        cacheMessagePublisher = new RedisClusterPublisher(hostAndPortsSet, "12345678");
        cacheMessagePublisher.start();


        ClusterCacheConfig<Long, Long> config = new ClusterCacheConfig<>();
        config.setCacheLoader(new CacheLoader<Long, Long>() {
            @Override
            public Long load(Long key) {
                loadCount += 1;
                System.out.println("load data from loader");
                if (key == 1) {
                    return null;
                }
                if (key == 2) {
                    throw new RuntimeException("test exception");
                }
                return (long)(key + 1);
            }
        });
        config.setRecordStatus(true);
        config.setRefreshAfterWrite(Duration.ofSeconds(3000));
        config.setMaximumSize(100);
        config.setCacheNullValue(true);
        config.setClusterName("localcachetest");
        config.setCacheName("test");
        config.setCacheMessagePublisher(cacheMessagePublisher);

        clusterCache = CacheBuilder.buildClusterCache(config);
        clusterCache2 = CacheBuilder.buildClusterCache(config);

        cacheMessageReceiver = new RedisClusterReceiver(Lists.newArrayList(clusterCache2, clusterCache));
        cacheMessageReceiver.start();
    }

    @Test
    public void testClusterCache() {
        long value = clusterCache.get(3L);
        Assert.assertEquals(4, value);

        value = clusterCache2.get(3L);
        Assert.assertEquals(4, value);

        clusterCache.remove(3L);
        Threads.sleepSeconds(2);

        int nextLoadCount = loadCount + 1;
        clusterCache2.get(3L);
        Assert.assertEquals(nextLoadCount, loadCount);
    }

    @Ignore
    @Test
    public void testStability() {
        for(;;){
            clusterCache.remove(3L);
            Threads.sleepSeconds(10);
        }
    }

    @Ignore
    @Test
    public void testClusterCache2() {
        log.info("联合页面测试清除缓存");
        long value = clusterCache2.get(3L);
        Assert.assertEquals(4, value);

        Threads.sleepSeconds(60);

        int nextLoadCount = loadCount + 1;
        clusterCache2.get(3L);
        Assert.assertEquals(nextLoadCount, loadCount);
    }


}
