package com.elephant.localcache.test;

import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import org.junit.runner.RunWith;

/**
 * 测试CASE打包
 */
@RunWith(WildcardPatternSuite.class)
@SuiteClasses({
        "**/*Test.class",
        "!*BaseTest.class"})
public class TestSuit {

}
