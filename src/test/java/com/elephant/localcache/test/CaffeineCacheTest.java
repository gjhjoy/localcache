package com.elephant.localcache.test;

import com.elephant.localcache.CacheBuilder;
import com.elephant.localcache.CacheConfig;
import com.elephant.localcache.api.Cache;
import com.elephant.localcache.api.CacheLoader;
import com.elephant.localcache.support.Threads;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;

/**
 * @author : gejianhua
 * @date 2021/1/29 19:12
 */
public class CaffeineCacheTest extends BaseTest {

    private static Cache<Integer, Integer> cache;
    private static Cache<Integer, Integer> nullVaueCache;
    private static int loadCount = 0;


    @BeforeClass
    public static void init() {
        CacheConfig<Integer, Integer> nullValueConfig = new CacheConfig<>();
        nullValueConfig.setCacheLoader(new CacheLoader<Integer, Integer>() {
            @Override
            public Integer load(Integer key) {
                loadCount += 1;
                System.out.println("load data from loader");
                if (key == 1) {
                    return null;
                }
                if (key == 2) {
                    throw new RuntimeException("test exception");
                }
                return key + 1;
            }
        });
        nullValueConfig.setRecordStatus(true);
        nullValueConfig.setRefreshAfterWrite(Duration.ofSeconds(1));
        nullValueConfig.setMaximumSize(100);
        nullValueConfig.setCacheNullValue(true);

        nullVaueCache = CacheBuilder.buildCache(nullValueConfig);

        CacheConfig<Integer, Integer> config = nullValueConfig.copy();
        config.setCacheNullValue(false);
        cache = CacheBuilder.buildCache(config);
    }

    @Test
    public void testCacheNullValue() {
        int nextLoadCount = loadCount + 1;
        Integer value = cache.get(1);
        Assert.assertNull(value);
        Assert.assertEquals(nextLoadCount, loadCount);

        nextLoadCount = loadCount + 1;
        value = cache.get(1);
        Assert.assertNull(value);
        Assert.assertEquals(nextLoadCount, loadCount);


        nextLoadCount = loadCount + 1;
        value = nullVaueCache.get(1);
        Assert.assertNull(value);
        Assert.assertEquals(nextLoadCount, loadCount);

        nextLoadCount = loadCount;
        value = nullVaueCache.get(1);
        Assert.assertNull(value);
        Assert.assertEquals(nextLoadCount, loadCount);
    }

    @Test
    public void testLoadException() {
        boolean exception = false;
        try {
            cache.get(2);
        } catch (Exception e) {
            e.printStackTrace();
            exception = true;
        }

        Assert.assertTrue(exception);
    }

    @Test
    public void testCacheExpire() {
        int value = cache.get(3);
        Assert.assertEquals(4, value);

        int nextLoadCount = loadCount;
        cache.get(3);
        Assert.assertEquals(nextLoadCount, loadCount);

        Threads.sleepSeconds(2);

        nextLoadCount = loadCount + 1;
        cache.get(3);
        Assert.assertEquals(nextLoadCount, loadCount);
    }
}





























