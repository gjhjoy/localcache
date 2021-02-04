package com.elephant.localcache.api;

import com.elephant.localcache.cluster.CacheMessage;

/**
 * @author : gejianhua
 * @date 2021/2/1 16:40
 */
public interface CacheMessageListener {

    void consumeMessage(CacheMessage cacheMessage);
}
