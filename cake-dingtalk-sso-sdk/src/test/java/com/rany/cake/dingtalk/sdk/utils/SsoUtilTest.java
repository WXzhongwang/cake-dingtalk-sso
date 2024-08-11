package com.rany.cake.dingtalk.sdk.utils;


import org.junit.Assert;
import org.junit.Test;

public class SsoUtilTest {


    @Test
    public void test() {
        String rootDomain = SsoUtil.getRootDomain("test.cake.rany.com");
        Assert.assertEquals(".rany.com", rootDomain);
    }

}