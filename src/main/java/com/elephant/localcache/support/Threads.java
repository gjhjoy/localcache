package com.elephant.localcache.support;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author : gejianhua
 * @date 2021/2/4 17:19
 */
@Slf4j
public class Threads {

    /**
     * 快捷创建新线程
     * @param target
     * @param name
     * @return
     */
    public static Thread newThread(Runnable target, String name) {
        Thread thread = new Thread(target);
        thread.setName(name);
        thread.setDaemon(true);
        return thread;
    }

    public static void sleep(TimeUnit timeUnit, long timeout) {
        try {
            timeUnit.sleep(timeout);
        }catch (Exception e){
            log.error("thread sleep exception", e);
        }
    }

    public static void sleepSeconds(int timeout){
        sleep(TimeUnit.SECONDS, timeout);
    }

    public static void sleepMilliseconds(int timeout){
        sleep(TimeUnit.MILLISECONDS, timeout);
    }
}
