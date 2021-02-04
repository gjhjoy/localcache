package com.elephant.localcache;

import lombok.Getter;
import lombok.Setter;

/**
 * @author : gejianhua
 * @date 2021/1/26 10:57
 */
@Getter
@Setter
public class CacheValueHolder<V> {

    private V value;

    public CacheValueHolder(V value) {
        this.value = value;
    }

    public static <V> CacheValueHolder valueOf(V value) {
        CacheValueHolder<V> holder = new CacheValueHolder(value);
        return holder;
    }



}
