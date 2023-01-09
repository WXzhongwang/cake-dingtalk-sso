package com.rany.cake.dingtalk.sdk.properties;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tutu
 */
@Slf4j
public class PropertiesManagerContainer {

    private PropertiesManagerContainer() {
    }

    private static Map map = new HashMap();
    private static String fileName;

    public static synchronized Object getProperties(Class cls){
        if (fileName == null) {
            throw new RuntimeException("PropertiesManagerContainer尚未初始化，");
        }
        if (map.containsKey(cls)) {
            return map.get(cls);

        } else {
            Object o = null;
            try {
                o = SsoConfigProperties.getProperties(fileName);
            } catch (Exception e) {
                log.error("获取sso客户端配置错误:" + e.getMessage(), e);
            }
            if (o == null) {
                throw new RuntimeException("无法获得" + cls.getName());
            }
            map.put(cls, o);
            return o;
        }

    }

    public static synchronized void init(final String _fileName) {
        if (fileName != null) {
            throw new RuntimeException("已经被初始化，无法再次初始化!");
        }
        fileName = _fileName;
        map = new HashMap(3);
    }


    public static synchronized void putBuapxClientProperties(SsoConfigProperties ssoConfigProperties) {
        map.put(SsoConfigProperties.class, ssoConfigProperties);
    }
}
