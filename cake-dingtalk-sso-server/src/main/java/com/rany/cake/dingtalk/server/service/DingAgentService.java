package com.rany.cake.dingtalk.server.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dingtalkcontact_1_0.models.GetUserHeaders;
import com.aliyun.dingtalkcontact_1_0.models.GetUserResponseBody;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiSnsGetuserinfoBycodeRequest;
import com.dingtalk.api.request.OapiUserGetUseridByUnionidRequest;
import com.dingtalk.api.response.OapiSnsGetuserinfoBycodeResponse;
import com.dingtalk.api.response.OapiUserGetUseridByUnionidResponse;
import com.rany.cake.dingtalk.server.properties.DingAppProperties;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
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
        } catch (TeaException err) {
            log.error("get user info error", err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.error("code:{}, message:{}", err.code, err.message);
            }

        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.error("code:{}, message:{}", err.code, err.message);
            }
        }
        return null;
    }

    public String getAccessToken(String authCode) {
        GetUserTokenRequest getUserTokenRequest = new GetUserTokenRequest()
                .setClientId(dingAppProperties.getAppKey())
                .setClientSecret(dingAppProperties.getAppSecret())
                .setCode(authCode)
                .setGrantType("authorization_code");
        GetUserTokenResponse getUserTokenResponse = null;
        try {
            getUserTokenResponse = authClient.getUserToken(getUserTokenRequest);
            // 获取用户个人token
            String accessToken = getUserTokenResponse.getBody().getAccessToken();
            return accessToken;
        } catch (Exception e) {
            log.error("get access token error", e);
        }
        return null;
    }

    public String getCorpAccessToken() {
        com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest getAccessTokenRequest = new com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest()
                .setAppKey(dingAppProperties.getAppKey())
                .setAppSecret(dingAppProperties.getAppSecret());
        try {
            GetAccessTokenResponse accessToken = authClient.getAccessToken(getAccessTokenRequest);
            return accessToken.getBody().getAccessToken();
        } catch (TeaException err) {
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }

        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }
        }
        return null;
    }

    public JSONObject getUserInfoByAuthCode(String tempAuthCode) {
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/sns/getuserinfo_bycode");
        OapiSnsGetuserinfoBycodeRequest req = new OapiSnsGetuserinfoBycodeRequest();
        req.setTmpAuthCode(tempAuthCode);
        try {
            OapiSnsGetuserinfoBycodeResponse response = client.execute(req, dingAppProperties.getAppKey(), dingAppProperties.getAppSecret());
            String result = response.getBody();
            JSONObject jsonObject = JSON.parseObject(result);
            JSONObject userInfo = (JSONObject) jsonObject.get("user_info");
            return userInfo;
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDingUserIdByUnionId(String accessToken, String unionId) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getUseridByUnionid");
        OapiUserGetUseridByUnionidRequest req = new OapiUserGetUseridByUnionidRequest();
        req.setUnionid(unionId);
        req.setHttpMethod("GET");
        OapiUserGetUseridByUnionidResponse rsp = null;
        try {
            rsp = client.execute(req, accessToken);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        String result = rsp.getBody();
        log.info(result);
        return result;
    }

    private static final String DINGTALK_BASE_URL = "https://oapi.dingtalk.com";

    public String getPersistentCode(String tmpAuthCode) {
        try {
            // 构建请求 URL
            String apiUrl = DINGTALK_BASE_URL + "/sns/get_persistent_code?accessKey=" + dingAppProperties.getAppKey() + "&accessSecret=" + dingAppProperties.getAppSecret();

            // 构建请求体
            String requestBody = "{\"tmp_auth_code\": \"" + tmpAuthCode + "\"}";

            // 发送 HTTP 请求
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(RequestBody.create(MediaType.get("application/json"), requestBody))
                    .build();

            Response response = client.newCall(request).execute();

            // 处理响应
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                // 解析响应获取持久授权码
                // 注意：实际应用中可能还需要处理异常、错误码等情况
                return responseBody;
            } else {
                // 处理请求失败的情况
                System.out.println("Request failed: " + response.code() + " - " + response.message());
            }
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
        }

        // 返回 null 表示获取失败
        return null;
    }

    public String getUserInfo(String persistentCode) {
        try {
            // 构建请求 URL
            String apiUrl = DINGTALK_BASE_URL + "/sns/get_sns_token?accessKey=" +
                    dingAppProperties.getAppKey() + "&accessSecret=" + dingAppProperties.getAppSecret();

            // 构建请求体
            String requestBody = "{\"persistent_code\": \"" + persistentCode + "\"}";

            // 发送 HTTP 请求
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(RequestBody.create(MediaType.get("application/json"), requestBody))
                    .build();

            Response response = client.newCall(request).execute();

            // 处理响应
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                // 解析响应获取用户信息
                // 注意：实际应用中可能还需要处理异常、错误码等情况
                return responseBody;
            } else {
                // 处理请求失败的情况
                System.out.println("Request failed: " + response.code() + " - " + response.message());
            }
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
        }

        // 返回 null 表示获取失败
        return null;
    }
}
