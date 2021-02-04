package com.elephant.localcache.api;

/**
 * @author : gejianhua
 * @date 2021/1/26 16:54
 */
public interface CacheMessageReceiver {

    /**
     * 启动
     */
    void start();

    /**
     * 销毁
     */
    void destory();


}
