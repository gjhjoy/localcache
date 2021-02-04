package com.elephant.localcache.cluster.rocketmq;

/**
 * @author : gejianhua
 * @date 2021/1/28 19:59
 */
public abstract class BaseRocketMqCluster {

    protected String topicName(String clusterName) {
        return "cluster_cache_" + clusterName + "_topic";
    }

    protected String consumerGroupName(String clusterName) {
        return "cluster_cache_" + clusterName + "_consumer_group";
    }

    protected static final String Default_Charset_Name = "utf-8";

    protected static final String RocketMq_Instance_Name = "ClusterCache";
}
