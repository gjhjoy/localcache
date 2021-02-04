package com.elephant.localcache;

import com.elephant.localcache.api.CacheLoader;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

/**
 * @author : gejianhua
 * @date 2021/1/26 11:08
 */
@Getter
@Setter
public class CacheConfig<K, V> {

    /**
     * 最大条数，必填项
     */
    private long maximumSize;

    /**
     * 是否缓存null value
     */
    private boolean cacheNullValue = false;
    /**
     * 是否记录状态
     */
    private boolean recordStatus = false;
    /**
     * 源数据加载程序，必填项
     */
    private CacheLoader<K, V> cacheLoader;
    /**
     * 访问多久之后过期  expireAfterAccess, expireAfterWrite, refreshAfterWrite至少填写一项
     */
    private Duration expireAfterAccess;
    /**
     * 写入多久之后过期  expireAfterAccess, expireAfterWrite, refreshAfterWrite至少填写一项
     */
    private Duration expireAfterWrite;
    /**
     * 写后多久刷新，缓存一直存在，到时间会从源数据加载最新数据  expireAfterAccess, expireAfterWrite, refreshAfterWrite至少填写一项
     */
    private Duration refreshAfterWrite;

    public CacheConfig<K, V> copy() {
        CacheConfig<K, V> config = new CacheConfig<>();
        config.setCacheNullValue(cacheNullValue);
        config.setMaximumSize(maximumSize);
        config.setRefreshAfterWrite(refreshAfterWrite);
        config.setRecordStatus(recordStatus);
        config.setCacheLoader(cacheLoader);
        config.setExpireAfterWrite(expireAfterWrite);
        config.setExpireAfterAccess(expireAfterAccess);
        return config;
    }




}
