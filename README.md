# cake-dingtalk-sso

**cake-dingtalk-sso** 是一个基于 SpringBoot + dingtalk内嵌网页登录的SSO 的统一认证服务端应用, 提供统一的登录认证能力。

1. SpringBoot 2.2.6.RELEASE
2. 底层使用uic-center 统一账号服务
4. redis访问缓存存储(jedis)
5. 支持app端token认证
6. 支持拓展账号密码登录
7. UIC模型请参考[uic-center](https://github.com/WXzhongwang/uic-center)
7. springboot 集成参考

**特性:**

- [x] **Java**  : 支持二开，设计简单，支持快速集成
- [x] **统一认证中心**  : 统一用户访问，只有一个用户中心
- [x] **钉钉网页嵌入方式**:  可围绕此方式搭建企业应用，一套登录，一套用户体系，打通内部统一用户诉求。
- [建设中] **用户不在企业内校验**: 边缘条件校验

![简单登录页参考](https://github.com/WXzhongwang/cake-dingtalk-sso/blob/main/WX20231023-204304%402x.png)
![接入成功登录成功](https://github.com/WXzhongwang/cake-dingtalk-sso/blob/main/WX20231023-204801@2x.png)

## 快速接入

### SpringBoot 快速接入

```xml

<dependency>
    <groupId>com.rany.cake.dingtalk.sso</groupId>
    <artifactId>cake-dingtalk-sso-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>

```

```java
package com.rany.cake.dingtalk.example.boot;

import com.rany.cake.dingtalk.starter.EnableCakeSso;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 应用入口
 *
 * @author zhongshengwang
 * @email 18668485565163.com
 */
@EnableCakeSso
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

```yaml
easy:
  sso:
    client:
      enable: true
      client-type: web
      sso-server: http://127.0.0.1:8765/
      login-url: http://127.0.0.1:8765/sso/loginPage
      logout-url: /sso/logout
      ignore-urls: /sso/loginPage,/sso/login,/oauth/login
      ignore-resources: .js, .css, .jpg, .png, .ico, .html
      ajax-failure-response:
        content-type: application/json
        code: 401
        msg: 用户未登录
    redis:
      address: redis://127.0.0.1:6379
```

### Spring接入

```xml

<dependency>
    <artifactId>cake-dingtalk-sso-sdk</artifactId>
    <groupId>com.rany.cake.dingtalk.sso</groupId>
    <version>1.0-SNAPSHOT</version>
</dependency>

```

初始化filter

```xml
<!DOCTYPE web-app PUBLIC
        "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
        "http://java.sun.com/dtd/web-app_2_3.dtd" >

<web-app>
    <display-name>Archetype Created Web Application</display-name>

    <filter>
        <filter-name>SsoWebFilter</filter-name>
        <filter-class>com.rany.cake.dingtalk.sdk.filter.SsoAuthenticationFilter</filter-class>
        <init-param>
            <param-name>configFile</param-name>
            <param-value>cake-sso-client.xml</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>SsoWebFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
</web-app>

```

自定义cake-ssl-client.xml, 配置sso远端地址

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE cakesso [
        <!ELEMENT cakesso (clientType+,ignoreUrls?,ignoreResources?,ssoServer+,logoutUrl+,loginUrl+,ajaxFailureResponse?, jedisConfig?)>
        <!ELEMENT clientType      (#PCDATA)>
        <!ELEMENT ignoreUrls    (#PCDATA)>
        <!ELEMENT ignoreResources (#PCDATA)>
        <!ELEMENT ssoServer    (#PCDATA)>
        <!ELEMENT logoutUrl    (#PCDATA)>
        <!ELEMENT loginUrl    (#PCDATA)>
        <!ELEMENT ajaxFailureResponse (contentType?,code?,msg?)>
        <!ELEMENT contentType    (#PCDATA)>
        <!ELEMENT code    (#PCDATA)>
        <!ELEMENT msg    (#PCDATA)>
        <!ELEMENT jedisConfig (address+, password?, maxTotal?, maxIdle?, minIdle?, maxWaitMillis?)>
        <!ELEMENT address    (#PCDATA)>
        <!ELEMENT password    (#PCDATA)>
        <!ELEMENT maxTotal    (#PCDATA)>
        <!ELEMENT maxIdle    (#PCDATA)>
        <!ELEMENT minIdle    (#PCDATA)>
        <!ELEMENT maxWaitMillis    (#PCDATA)>
        ]>
<cakesso>
    <!-- 应用类型 -->
    <clientType>web</clientType>
    <!-- 不需要验证的路径 -->
    <ignoreUrls>
        /login
        /sso/login
        /sso/loginPage
    </ignoreUrls>
    <!-- 不需要拦截的静态资源 -->
    <ignoreResources>
        .js
        .css
        .jpg
        .png
        .ico
        .html
    </ignoreResources>
    <!-- SSO Server地址 -->
    <ssoServer>http://127.0.0.1:8888/</ssoServer>
    <!-- 注销接口地址 -->
    <logoutUrl>/sso/logout</logoutUrl>
    <!-- 登录地址完整路径 -->
    <loginUrl>http://127.0.0.1:8888/sso/loginPage</loginUrl>
    <!-- ajax请求认证失败响应配置 -->
    <ajaxFailureResponse>
        <contentType>application/json</contentType>
        <code>401</code>
        <msg>用户未登录</msg>
    </ajaxFailureResponse>
    <!-- redis配置,客户端类型为app时需要配置 -->
    <!--
    <jedisConfig>
        <address>redis://127.0.0.1:6379</address>
        <maxTotal>200</maxTotal>
        <maxIdle>50</maxIdle>
        <minIdle>8</minIdle>
        <maxWaitMillis>10000</maxWaitMillis>
    </jedisConfig>
    -->
</cakesso>
```