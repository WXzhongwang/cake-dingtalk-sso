package com.rany.cake.dingtalk.server.rest;

import com.alibaba.fastjson.JSONObject;
import com.rany.cake.dingtalk.sdk.beans.SsoUser;
import com.rany.cake.dingtalk.sdk.configuration.SsoConstants;
import com.rany.cake.dingtalk.sdk.properties.SsoConfigProperties;
import com.rany.cake.dingtalk.sdk.utils.SsoUtil;
import com.rany.cake.dingtalk.server.properties.SsoServerProperties;
import com.rany.cake.dingtalk.server.service.DingAgentService;
import com.rany.cake.dingtalk.starter.SsoProperties;
import com.rany.ops.api.command.account.CreateAccountCommand;
import com.rany.ops.api.facade.account.AccountFacade;
import com.rany.ops.api.query.account.AccountDingIdQuery;
import com.rany.ops.common.dto.account.AccountDTO;
import com.rany.ops.common.enums.AccountTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


/**
 * @author tutu
 */
@Slf4j
@Api(tags = "WEB客户端登录相关接口")
@Controller
public class WebController {

    @Autowired
    private SsoProperties ssoProperties;

    @Autowired
    private SsoServerProperties serverProperties;

    @Autowired
    private DingAgentService dingAgentService;

    @Autowired
    private AccountFacade accountFacade;

    @Value("${cake.uic.tenantId:768460649511661568}")
    private Long tenantId;

    /**
     * web客户端登录页
     *
     * @param webapp
     * @param model
     * @return
     */
    @ApiOperation("跳转登录页")
    @GetMapping("/sso/loginPage")
    private String loginPage(String webapp, ModelMap model) {
        model.put("webapp", webapp);
        model.put("appId", "dingdmo8khbuyyvgvcmi");
        model.put("redirectUri", ssoProperties.getSsoCallbackUrl());
        return "login";
    }

    @ApiOperation("code")
    @RequestMapping(value = "/sso/code", method = RequestMethod.GET)
    private String code(@RequestParam(value = "code", required = true) String code,
                        @RequestParam(value = "state") String webapp,
                        HttpServletRequest request, HttpServletResponse response
            , ModelMap model) throws IOException {
        //String accessToken = dingAgentService.getAccessToken(code);
        //GetUserResponseBody userinfo = dingAgentService.getUserinfo(accessToken);

        JSONObject userInfoByAuthCode = dingAgentService.getUserInfoByAuthCode(code);

        if (!ObjectUtils.isEmpty(userInfoByAuthCode)) {
            String unionId = userInfoByAuthCode.getString("unionid");
            String openid = userInfoByAuthCode.getString("openid");
            String nick = userInfoByAuthCode.getString("nick");
            SsoConfigProperties properties = new SsoConfigProperties();
            BeanUtils.copyProperties(ssoProperties, properties);

            AccountDingIdQuery accountBasicQuery = new AccountDingIdQuery();
            accountBasicQuery.setTenantId(tenantId);
            accountBasicQuery.setDingUnionId(unionId);

            AccountDTO content = null;
            try {
                content = accountFacade.getAccountByDingId(accountBasicQuery);
            } catch (Exception ex) {
                log.error("获取用户信息失败", ex);

                // 尝试注册用户
                try {
                    CreateAccountCommand createAccountCommand = new CreateAccountCommand();
                    createAccountCommand.setTenantId(tenantId);
                    createAccountCommand.setAccountName(nick);
                    createAccountCommand.setOpenId(openid);
                    createAccountCommand.setDingUnionId(unionId);
                    createAccountCommand.setAccountType(AccountTypeEnum.BASIC.name());
                    Long newAccount = accountFacade.createAccount(createAccountCommand);
                    if (newAccount == null) {
                        model.put("msg", SsoConstants.ACCOUNT_REGISTER_FAILED);
                        return "login";
                    }
                } catch (Exception exception) {
                    model.put("msg", SsoConstants.ACCOUNT_REGISTER_FAILED);
                    return "login";
                }
            }


            // 注册完再次查询
            accountBasicQuery.setDingUnionId(unionId);
            content = accountFacade.getAccountByDingId(accountBasicQuery);

            SsoUser ssoUser = SsoUser.builder()
                    .userId(String.valueOf(content.getId()))
                    .userName(content.getAccountName())
                    .build();

            // 将用户信息保存到Cookie
            SsoUtil.saveCookie(request, response, SsoConstants.SSO_SESSION_ID,
                    JSONObject.toJSONString(ssoUser), serverProperties.getMaxAge());

            // 设置session过期时间和Cookie过期时间同步
            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(serverProperties.getMaxAge());

            //重定向到原先访问的页面
            if (StringUtils.isEmpty(webapp)) {
                return "redirect:/admin";
            } else {
                String url = StringUtils.EMPTY;
                try {
                    url = "redirect:" + URLDecoder.decode(webapp, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    log.error(e.getMessage(), e);
                }
                return url;
            }
        } else {
            model.put("msg", SsoConstants.AUTH_FAILED_WARN);
            return "login";
        }
    }


    /**
     * web客户端用户登录
     *
     * @param username
     * @param password
     * @param webapp
     * @param request
     * @param response
     * @param model
     * @return
     */
    @ApiOperation("用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", required = true),
            @ApiImplicitParam(name = "password", required = true),
            @ApiImplicitParam(name = "webapp")
    })
    @RequestMapping(value = "/sso/login", method = RequestMethod.POST)
    public String login(@RequestParam(value = "username", required = true) String username,
                        @RequestParam(value = "password", required = true) String password,
                        @RequestParam(SsoConstants.WEBAPP) String webapp,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        ModelMap model) {

//        User user = userService.getByUserNameAndPassword(username, password);
//
//        if (!ObjectUtils.isEmpty(user)) {
//            SsoConfigProperties properties = new SsoConfigProperties();
//            BeanUtils.copyProperties(ssoProperties, properties);
//            SsoUser ssoUser = SsoUser.builder()
//                    .userId(String.valueOf(user.getId()))
//                    .userName(user.getUserName())
//                    .build();
//
//            // 将用户信息保存到Cookie
//            SsoUtil.saveCookie(request, response, SsoConstants.SSO_SESSION_ID,
//                    JSONObject.toJSONString(ssoUser), serverProperties.getMaxAge());
//
//            // 设置session过期时间和Cookie过期时间同步
//            HttpSession session = request.getSession();
//            session.setMaxInactiveInterval(serverProperties.getMaxAge());
//
//            //重定向到原先访问的页面
//            if (StringUtils.isEmpty(webapp)) {
//                return "redirect:/admin";
//            } else {
//                String url = StringUtils.EMPTY;
//                try {
//                    url = "redirect:" + URLDecoder.decode(webapp, "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    log.error(e.getMessage(), e);
//                }
//                return url;
//            }
//        } else {
//            model.put("msg", SsoConstants.AUTH_FAILED_WARN);
//            return "login";
//        }
        return null;
    }


    /**
     * 描述: web客户端退出登录
     *
     * @param request
     * @param response
     * @return {@link String}
     * @author maming
     * @date 2021/11/12
     */
    @ApiOperation("用户注销")
    @RequestMapping(value = "/sso/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request,
                         HttpServletResponse response) {
        SsoUtil.invalidateSession(request);
        SsoUtil.removeCookie(request, response, SsoConstants.SSO_SESSION_ID);
        return "redirect:/login";
    }

}
