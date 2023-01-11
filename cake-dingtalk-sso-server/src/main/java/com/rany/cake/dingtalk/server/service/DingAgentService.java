package com.rany.cake.dingtalk.server.service;

import com.aliyun.dingtalkcontact_1_0.models.GetUserHeaders;
import com.aliyun.dingtalkcontact_1_0.models.GetUserResponseBody;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.rany.cake.dingtalk.server.properties.DingAppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2023/1/11 21:46
 * @email 18668485565163.com
 */
@Component
@Slf4j
public class DingAgentService {

    @Resource
    private DingAppProperties dingAppProperties;
    private static com.aliyun.dingtalkoauth2_1_0.Client authClient;
    private static com.aliyun.dingtalkcontact_1_0.Client contactClient;

    static {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        try {
            DingAgentService.authClient = new com.aliyun.dingtalkoauth2_1_0.Client(config);
            DingAgentService.contactClient = new com.aliyun.dingtalkcontact_1_0.Client(config);
        } catch (Exception e) {
            log.error("client create error", e);
        }
    }

    /**
     * 获取用户个人信息
     *
     * @param accessToken
     * @return
     * @throws Exception
     */
    public GetUserResponseBody getUserinfo(String accessToken) {
        GetUserHeaders getUserHeaders = new GetUserHeaders();
        getUserHeaders.xAcsDingtalkAccessToken = accessToken;
        try {
            return contactClient.getUserWithOptions("me", getUserHeaders, new RuntimeOptions()).getBody();
        } catch (Exception e) {
            log.error("get user info error", e);
        }
        return null;
    }

    public String getAccessToken(String authCode) {
        GetUserTokenRequest getUserTokenRequest = new GetUserTokenRequest()
                .setClientId(dingAppProperties.getAgentId())
                .setClientSecret(dingAppProperties.getAppSecret())
                .setCode(authCode)
                .setGrantType("authorization_code");
        GetUserTokenResponse getUserTokenResponse = null;
        try {
            getUserTokenResponse = authClient.getUserToken(getUserTokenRequest);
            // 获取用户个人token
            String accessToken = getUserTokenResponse.getBody().getAccessToken();
        } catch (Exception e) {
            log.error("get access token error", e);
        }
        return null;
    }
}
