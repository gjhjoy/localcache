package com.elephant.localcache.support;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * @author : gejianhua
 * @date 2021/2/4 17:39
 */
public class Printable implements Serializable {

    @Override
    public String toString() {
        try {
            return this.getClass().getSimpleName() + "(" + JSON.toJSONStringWithDateFormat(this, "yyyy-MM-dd HH:mm:ss.SSS") + ")";
        } catch (Exception e) {
            return "NULL";
        }
    }
}
