package com.elephant.localcache.cluster.rocketmq;

import com.alibaba.fastjson.JSON;
import com.elephant.localcache.api.CacheMessagePublisher;
import com.elephant.localcache.cluster.CacheMessage;
import com.elephant.localcache.support.ClusterKeyTypeUtils;
import com.elephant.localcache.support.LocalCacheException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.List;

/**
 * @author : gejianhua
 * @date 2021/1/27 11:05
 */
@Slf4j
public class RocketMqClusterPublisher extends BaseRocketMqCluster implements CacheMessagePublisher {

    private String namesrvAddr;
    private DefaultMQProducer defaultMQProducer;

    public RocketMqClusterPublisher(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }


    @Override
    public void start() {
        log.info("正在创建MQ Producer实例");
        try {
            defaultMQProducer = new DefaultMQProducer("cluster_cache_publish_group");
            defaultMQProducer.setNamesrvAddr(namesrvAddr);
            defaultMQProducer.setVipChannelEnabled(false);
            defaultMQProducer.setInstanceName(RocketMq_Instance_Name);
            defaultMQProducer.start();
        } catch (Exception e) {
            throw new LocalCacheException("mq producer start exception", e);
        }
    }

    @Override
    public void destory() {
        if (defaultMQProducer != null) {
            defaultMQProducer.shutdown();
        }
    }

    @Override
    public boolean publish(CacheMessage cacheMessage) {
        try {
            log.info("start publish cache message:{}", cacheMessage);
            ClusterKeyTypeUtils.checkTypeSupport(cacheMessage.getKeys());
            Message message = new Message();
            message.setBody(JSON.toJSONString(cacheMessage).getBytes(Default_Charset_Name));
            message.setTopic(topicName(cacheMessage.getClusterName()));
            defaultMQProducer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.error("rocketmq send cache message success, {}", cacheMessage);
                }

                @Override
                public void onException(Throwable e) {
                    log.error("rocketmq send cache message exception, {}", cacheMessage, e);
                }
            });
            return true;
        } catch (Exception e) {
            log.error("rocketmq cluster cache publish message exception, {}", cacheMessage, e);
            return false;
        }
    }


}
