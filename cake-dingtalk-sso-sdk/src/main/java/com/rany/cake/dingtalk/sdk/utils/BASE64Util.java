package com.rany.cake.dingtalk.sdk.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@Slf4j
public class BASE64Util {


    public static String encode(String data) {
        String encodeStr = StringUtils.EMPTY;
        try {
            encodeStr = new BASE64Encoder().encode(data.getBytes("UTF-8"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return encodeStr;
    }



    public static String decode(String data) {
        String decodeStr = StringUtils.EMPTY;
        try {
            decodeStr = new String(new BASE64Decoder().decodeBuffer(data),"UTF-8");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return decodeStr;
    }

}
