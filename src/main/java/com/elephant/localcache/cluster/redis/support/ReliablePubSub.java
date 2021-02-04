package com.elephant.localcache.cluster.redis.support;

import com.alibaba.fastjson.JSON;
import com.elephant.localcache.support.LocalCacheException;
import com.elephant.localcache.support.Threads;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
public class ReliablePubSub extends JedisPubSub {

    private static final long subscribeIntervalSeconds = 10;
    private static final long heartbeatIntervalSeconds = 10;

    private AtomicBoolean isStartSubscribe = new AtomicBoolean(false);
    private JedisCluster jedisCluster;

    public ReliablePubSub(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    @Override
    public void subscribe(String... channels) {
        if (channels == null || channels.length == 0) {
            throw new LocalCacheException("redis reliable subscribe failed, channels is empty!");
        }

        if (!isStartSubscribe.compareAndSet(false, true)) {
            return;
        }

        Threads.newThread(() -> {
            for (; ; ) {
                try {
                    jedisCluster.subscribe(this, channels);//订阅成功线程阻塞在此
                } catch (Exception e) {
                    log.error("subscribe redis channel exception, channels:{}", JSON.toJSONString(channels));
                    try {
                        super.unsubscribe(channels);
                    } catch (Exception ex) {
                        log.error("unsubscribe redis channel exception, channels:{}", JSON.toJSONString(channels));
                    }

                    Threads.sleep(TimeUnit.SECONDS, subscribeIntervalSeconds);
                }
            }
        }, "redis_cluster_cache_subscribe").start();

        heartbeat();
    }


    private void heartbeat() {
        Threads.newThread(() -> {
            Threads.sleep(TimeUnit.SECONDS, 10);//休眠10s， 等待订阅成功
            for (; ; ) {
                try {
                    log.info("send ping start");
                    ping();
                } catch (Exception e) {
                    log.error("redis channel send ping exception", e);
                } finally {
                    Threads.sleep(TimeUnit.SECONDS, heartbeatIntervalSeconds);
                }
            }
        }, "redis_channel_ping_pong_thread").start();
    }


    @Override
    public void onPong(String pattern) {
        log.info("redis channel pong :{}", pattern);
        super.onPong(pattern);
    }


    @SuppressWarnings("unchecked")
    @Override
    public void onMessage(String channel, String message) {
        super.onMessage(channel, message);
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        log.info("start subscribe channel:{}", channel);
        super.onSubscribe(channel, subscribedChannels);

    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        log.info("unsubscribe channel:{}", channel);
        super.onUnsubscribe(channel, subscribedChannels);
    }


}

