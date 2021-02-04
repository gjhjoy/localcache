package com.elephant.localcache.cluster.rocketmq;

import com.elephant.localcache.api.CacheMessageReceiver;
import com.elephant.localcache.api.ClusterCache;
import com.elephant.localcache.cluster.CacheMessageProcessor;
import com.elephant.localcache.support.LocalCacheException;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author : gejianhua
 * @date 2021/1/27 11:05
 */
@Slf4j
public class RocketMqClusterReceiver extends BaseRocketMqCluster implements CacheMessageReceiver {

    private List<ClusterCache> clusterCaches;
    private DefaultMQPushConsumer consumer;
    private String namesrvAddr;
    private CacheMessageProcessor cacheMessageProcessor;

    public RocketMqClusterReceiver(List<ClusterCache> clusterCaches, String namesrvAddr) {
        if (CollectionUtils.isEmpty(clusterCaches)) {
            throw new LocalCacheException("cluster cache is empty");
        }
        this.clusterCaches = clusterCaches;
        this.namesrvAddr = namesrvAddr;
        cacheMessageProcessor = new CacheMessageProcessor(clusterCaches);
    }

    @Override
    public void start() {
        Set<String> topicNames = topicNames();
        if (topicNames.size() > 1) {
            throw new LocalCacheException("cluster name must one!");
        }

        consumer = new DefaultMQPushConsumer();
        consumer.setNamesrvAddr(namesrvAddr);

        Map<String, String> subscriptionMap = Maps.newHashMap();
        for (String topic : topicNames) {
            subscriptionMap.put(topic, "*");
        }
        consumer.setSubscription(subscriptionMap);

        consumer.setConsumerGroup(consumerGroupName(clusterCaches.get(0).clusterName()));
        consumer.setMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {
                    try {
                        cacheMessageProcessor.process(new String(msg.getBody(), Charset.forName(Default_Charset_Name)));
                    } catch (Exception e) {
                        log.error("rocketmq message listener process message exception", e);
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.setMessageModel(MessageModel.BROADCASTING);
        consumer.setInstanceName(RocketMq_Instance_Name);
        consumer.setConsumeThreadMin(1);
        consumer.setConsumeThreadMin(Runtime.getRuntime().availableProcessors());
        try {
            consumer.start();
        } catch (Exception e) {
            throw new LocalCacheException("start rocketmq consumer exception", e);
        }
    }

    @Override
    public void destory() {
        if (consumer != null) {
            consumer.shutdown();
        }
    }

    private Set<String> topicNames() {
        Set<String> topicSet = Sets.newHashSet();
        clusterCaches.forEach(clusterCache -> {
            topicSet.add(topicName(clusterCache.clusterName()));
        });
        return topicSet;
    }

}
