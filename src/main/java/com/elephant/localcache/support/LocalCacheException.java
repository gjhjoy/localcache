package com.elephant.localcache.support;

/**
 * @author : gejianhua
 * @date 2021/1/26 21:42
 */
public class LocalCacheException extends RuntimeException {

    public LocalCacheException(String errMsg) {
        super(errMsg);
    }

    public LocalCacheException(String errMsg, Throwable throwable) {
        super(errMsg, throwable);
    }
}
