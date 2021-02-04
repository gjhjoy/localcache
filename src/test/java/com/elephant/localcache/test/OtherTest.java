package com.elephant.localcache.test;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.List;

/**
 * @author : gejianhua
 * @date 2021/1/27 20:52
 */
public class OtherTest {

    @Test
    public void testGenericType() {
        List<Integer> intList = Lists.newArrayList(1);

        List<Object> objList = Lists.newArrayList();
        objList.add(intList.get(0));
        objList.add("dfasdf");

        Assert.assertEquals(Integer.class.getCanonicalName(), objList.get(0).getClass().getCanonicalName());
        Assert.assertEquals(String.class.getCanonicalName(), objList.get(1).getClass().getCanonicalName());

        String a = "";
        Object b = a;
        Assert.assertEquals(String.class.getCanonicalName(), b.getClass().getCanonicalName());

        compareClassName(objList);
    }

    private <K> void compareClassName(List<K> list) {
        Assert.assertEquals(Integer.class.getCanonicalName(), list.get(0).getClass().getCanonicalName());
        Assert.assertEquals(String.class.getCanonicalName(), list.get(1).getClass().getCanonicalName());
    }


    @Test
    public void testDuration() {
        System.out.println(Duration.ofSeconds(10).toMillis());
    }


}
