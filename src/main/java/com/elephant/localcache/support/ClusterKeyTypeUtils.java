package com.elephant.localcache.support;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author : gejianhua
 * @date 2020/3/12 19:12
 * 集群缓存对key的支持
 */
public class ClusterKeyTypeUtils {

    private static Map<Class, Function> supportKeyClassMap;

    static {
        supportKeyClassMap = Maps.newHashMap();

        supportKeyClassMap.put(Integer.class, (obj) -> {
            return Integer.valueOf(String.valueOf(obj));
        });

        supportKeyClassMap.put(String.class, (obj) -> {
            return obj;
        });

        supportKeyClassMap.put(Byte.class, (obj) -> {
            return Byte.valueOf(String.valueOf(obj));
        });

        supportKeyClassMap.put(Short.class, (obj) -> {
            return Short.valueOf(String.valueOf(obj));
        });

        supportKeyClassMap.put(Long.class, (obj) -> {
            return Long.valueOf(String.valueOf(obj));
        });

        supportKeyClassMap.put(BigDecimal.class, (obj) -> {
            return new BigDecimal(String.valueOf(obj));
        });

        supportKeyClassMap.put(Double.class, (obj) -> {
            return Double.valueOf(String.valueOf(obj));
        });

        supportKeyClassMap.put(Boolean.class, (obj) -> {
            return Boolean.valueOf(String.valueOf(obj));
        });

        supportKeyClassMap.put(Float.class, (obj) -> {
            return Float.valueOf(String.valueOf(obj));
        });
    }

    /**
     * 获取key的真实类型值
     *
     * @param key
     * @param keyClassName
     * @return
     * @throws ClassNotFoundException
     */
    public static Object getRealTypeKey(Object key, String keyClassName) {
        if (key == null) {
            throw new LocalCacheException("key is must not null");
        }
        if (StringUtils.isBlank(keyClassName)) {
            throw new LocalCacheException("key className is must not blank");
        }

        try {
            Function function = supportKeyClassMap.get(Class.forName(keyClassName));
            if (function == null) {
                throw new LocalCacheException("key class not support");
            }

            return function.apply(key);
        } catch (Exception e) {
            throw new LocalCacheException("get key real type exception", e);
        }
    }

    /**
     * 是否支持指定的类型
     *
     * @param clazz
     * @return
     */
    public static boolean isSupport(Class clazz) {
        return supportKeyClassMap.containsKey(clazz);
    }

    public static void checkTypeSupport(Class clazz) {
        if (!supportKeyClassMap.containsKey(clazz)) {
            throw new LocalCacheException("key type is not support!");
        }
    }

    public static <K> void checkTypeSupport(K key) {
        checkTypeSupport(key.getClass());
    }

    public static <K> void checkTypeSupport(List<K> keys) {
        checkTypeSupport(keys.get(0));
    }
}































