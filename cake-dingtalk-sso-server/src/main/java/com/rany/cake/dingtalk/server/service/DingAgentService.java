package com.rany.cake.dingtalk.server.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.teaopenapi.models.Config;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiSnsGetuserinfoBycodeRequest;
import com.dingtalk.api.response.OapiSnsGetuserinfoBycodeResponse;
import com.rany.cake.dingtalk.server.properties.DingAppProperties;
import com.taobao.api.ApiException;
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
}
