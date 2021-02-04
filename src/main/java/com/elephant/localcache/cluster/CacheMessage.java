package com.elephant.localcache.cluster;

import com.elephant.localcache.support.Printable;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author : gejianhua
 * @date 2021/1/26 16:51
 */
@Getter
@Setter
public class CacheMessage extends Printable {

    private String clusterName;

    private String cacheName;

    private List<Object> keys;

    private String keyClassName;
}
