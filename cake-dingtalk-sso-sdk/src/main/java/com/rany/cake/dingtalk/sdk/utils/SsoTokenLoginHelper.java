package com.rany.cake.dingtalk.sdk.utils;

import com.alibaba.fastjson.JSON;
import com.rany.cake.dingtalk.sdk.beans.SsoUser;
import com.rany.cake.dingtalk.sdk.configuration.SsoConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author tutu
 */
@Slf4j
public class SsoTokenLoginHelper {

    /**
    * 描述:根据key存储Token登录用户
    * @author maming
    * @date 2021/11/12
    * @param storeKey
    * @param ssoUser
    * @param seconds
    * @return
    */
    public static void storageUser(String storeKey, SsoUser ssoUser, int seconds){
        JedisUtil.setStringValue(SsoConstants.STORAGE_USER_PREFIX + storeKey, JSON.toJSONString(ssoUser), seconds);
    }


    /**
    * 描述:根据key删除Token登录用户
    * @author maming
    * @date 2021/11/12
    * @param storeKey
    * @return
    */
    public static void removeStorageUser(String storeKey){
        JedisUtil.del(SsoConstants.STORAGE_USER_PREFIX + storeKey);
    }


    /**
    * 描述: 获取key获取存储用户信息
    * @author maming
    * @date 2021/11/12
    * @param storeKey
    * @return {@link SsoUser}
    */
    public static SsoUser getStorageUser(String storeKey){
        String value = JedisUtil.getStringValue(SsoConstants.STORAGE_USER_PREFIX + storeKey);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return JSON.parseObject(value, SsoUser.class);
    }


    /**
    * 描述:校验用户是否已登录
    * @author maming
    * @date 2021/11/12
    * @param storeKey
    * @return {@link boolean}
    */
    public static boolean isLogined(HttpServletRequest request, String storeKey){
        return JedisUtil.exists(SsoConstants.STORAGE_USER_PREFIX + storeKey);
    }
}
