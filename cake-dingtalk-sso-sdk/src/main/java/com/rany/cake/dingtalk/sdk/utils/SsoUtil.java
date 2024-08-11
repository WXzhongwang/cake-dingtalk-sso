package com.rany.cake.dingtalk.sdk.utils;

import com.alibaba.fastjson.JSON;
import com.rany.cake.dingtalk.sdk.beans.AjaxResponse;
import com.rany.cake.dingtalk.sdk.beans.SsoUser;
import com.rany.cake.dingtalk.sdk.configuration.SsoConstants;
import com.rany.cake.dingtalk.sdk.properties.AjaxFailureResponse;
import com.rany.cake.dingtalk.sdk.properties.SsoConfigProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;

/**
 * @author tutu
 */
@Slf4j
public class SsoUtil {

    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();


    /**
     * 描述:设置请求跨域
     *
     * @param request
     * @param response
     * @return
     * @author maming
     * @date 2021/11/12
     */
    public static void setRequestCross(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "*");
    }


    /**
     * 描述:判断是否允许直接放行
     *
     * @param path
     * @param ssoProperties
     * @return {@link boolean}
     * @author maming
     * @date 2021/11/12
     */
    public static boolean isAllow(String path, SsoConfigProperties ssoProperties) {

        // 获取Resource白名单
        String[] ignoreResources = ssoProperties.getIgnoreResources();

        for (String ignoreResource : ignoreResources) {
            if (path.endsWith(ignoreResource)) {
                return true;
            }
        }

        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        // 获取URL白名单
        String[] ignoredPaths = ssoProperties.getIgnoreUrls();
        for (String ignoredPath : ignoredPaths) {
            if (PATH_MATCHER.match(ignoredPath, path)) {
                return true;
            }
        }

        return false;
    }


    /**
     * 描述: 判断是否已登录
     *
     * @param request
     * @param response
     * @return {@link boolean}
     * @author maming
     * @date 2021/11/8
     */
    public static boolean isLogined(HttpServletRequest request, HttpServletResponse response) {
        // 默认未登录
        boolean isLogined = false;

        // 1、先从session中读取
        SsoUser currentUser = (SsoUser) request.getSession().getAttribute(SsoConstants.CURRENT_USER);
        if (!ObjectUtils.isEmpty(currentUser)) {
            return true;
        }


        // 2、通过key获取cookie信息
        String cookieVal = SsoUtil.getCookieVal(request, SsoConstants.SSO_SESSION_ID);

        // 3、若session或cookie中都没有用户信息,则无法访问
        if (StringUtils.isEmpty(cookieVal)) {
            return false;
        }

        SsoUser ssoUser = JSON.parseObject(cookieVal, SsoUser.class);

        if (!isLogined && !ObjectUtils.isEmpty(ssoUser)) {
            // 标记为已登录
            isLogined = true;
            // 将用户信息存储到session
            setCurrentUser(request, ssoUser);
        }

        return isLogined;
    }


    /**
     * 描述:设置Cookie
     *
     * @param request
     * @param response
     * @param key
     * @param value
     * @param maxAge
     * @return
     * @author maming
     * @date 2021/11/8
     */
    @SneakyThrows
    public static void saveCookie(HttpServletRequest request, HttpServletResponse response,
                                  String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, URLEncoder.encode(BASE64Util.encode(value), "UTF-8"));
        cookie.setMaxAge(maxAge);
        //设置访问路径
        cookie.setPath("/");
        String rootDomain = getRootDomain(request.getServerName());
        log.info("cookie root domain:" + rootDomain);
        cookie.setDomain(rootDomain);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }


    // 辅助方法获取根域名
    private static String getRootDomain(String serverName) {
        if (serverName.contains(".")) {
            String[] parts = serverName.split("\\.");
            StringBuilder rootDomain = new StringBuilder(".");
            for (int i = parts.length - 1; i > 0; i--) {
                rootDomain.insert(0, parts[i] + ".");
            }
            return rootDomain.toString();
        }
        return serverName;
    }

    /**
     * 描述:删除Cookie
     *
     * @param request
     * @param response
     * @param key
     * @return
     * @author maming
     * @date 2021/11/12
     */
    public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (key.equals(cookie.getName())) {
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
    }


    /**
     * 描述:通过key获取Cookie值
     *
     * @param request
     * @param key
     * @return {@link String}
     * @author maming
     * @date 2021/11/8
     */
    @SneakyThrows
    public static String getCookieVal(HttpServletRequest request, String key) {
        String value = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (key.equals(cookie.getName())) {
                    String valueDecode = URLDecoder.decode(cookie.getValue(), "UTF-8");
                    value = BASE64Util.decode(valueDecode);
                }
            }
        }
        return value;
    }


    /**
     * 描述:设置当前登录用户
     *
     * @param request
     * @param ssoUser
     * @return
     * @author maming
     * @date 2021/11/8
     */
    public static void setCurrentUser(HttpServletRequest request, SsoUser ssoUser) {
        HttpSession session = request.getSession();
        if (session.getAttribute(SsoConstants.CURRENT_USER) == null) {
            session.setAttribute(SsoConstants.CURRENT_USER, ssoUser);
        }
    }


    /**
     * 描述: 获取当前登录用户
     *
     * @param request
     * @return {@link SsoUser}
     * @author maming
     * @date 2021/11/10
     */
    public static SsoUser getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session == null) {
            return null;
        }

        SsoUser ssoUser = (SsoUser) session.getAttribute(SsoConstants.CURRENT_USER);
        if (ObjectUtils.isEmpty(ssoUser)) {
            String cookieVal = SsoUtil.getCookieVal(request, SsoConstants.SSO_SESSION_ID);

            if (StringUtils.isEmpty(cookieVal)) {
                return null;
            }

            ssoUser = JSON.parseObject(cookieVal, SsoUser.class);
        }

        return ssoUser;
    }


    /**
     * 描述: 清除session
     *
     * @param request
     * @return
     * @author maming
     * @date 2021/11/9
     */
    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession();

        if (session == null) {
            return;
        }

        Enumeration<String> names = session.getAttributeNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            session.removeAttribute(name);
        }

        session.invalidate();
    }


    /**
     * 描述:跳转登录页
     *
     * @param request
     * @param response
     * @param ssoProperties
     * @return
     * @author maming
     * @date 2021/11/8
     */
    public static void redirectLogin(HttpServletRequest request,
                                     HttpServletResponse response,

                                     SsoConfigProperties ssoProperties) throws IOException {
        // 访问路径(完整)
        String url = request.getRequestURL().toString();
        // 访问路径(部分)
        String path = request.getRequestURI();
        String ssoServer = ssoProperties.getSsoServer();

        if (!ssoServer.endsWith("/")) {
            ssoServer += "/";
        }

        if (ssoProperties.getLogoutUrl().equals(path)) {
            // 当前服务地址
            String link = url.replace(path, "");
            url = String.format("%s%s%s%s%s", ssoServer, "sso/loginPage?", SsoConstants.WEBAPP, "=", URLEncoder.encode(link, "UTF-8"));
        } else {
            url = String.format("%s%s%s%s%s", ssoServer, "sso/loginPage?", SsoConstants.WEBAPP, "=", URLEncoder.encode(url, "UTF-8"));
        }

        response.sendRedirect(url);
    }


    /**
     * 描述:判断是否为ajax请求
     *
     * @param request
     * @return {@link boolean}
     * @author maming
     * @date 2021/11/12
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return true;
        }

        return false;
    }


    /**
     * 描述:ajax认证失败响应
     *
     * @param response
     * @param ssoProperties
     * @return
     * @author maming
     * @date 2021/11/12
     */
    public static void ajaxAuthFailure(HttpServletResponse response, SsoConfigProperties ssoProperties) {
        AjaxFailureResponse ajaxFailureResponse = ssoProperties.getAjaxFailureResponse();
        response.setContentType(ajaxFailureResponse.getContentType());
        response.setStatus(ajaxFailureResponse.getCode());

        AjaxResponse ajaxResponse = AjaxResponse.builder()
                .code(ajaxFailureResponse.getCode())
                .msg(ajaxFailureResponse.getMsg())
                .loginUrl(ssoProperties.getLoginUrl())
                .build();
        try {
            response.getWriter().write(JSON.toJSONString(ajaxResponse));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
