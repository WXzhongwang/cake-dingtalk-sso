package com.rany.cake.dingtalk.sdk.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tutu
 */
@Setter
@Getter
public class AjaxFailureResponse {

    private Integer code = 401;

    private String msg = "用户未登录";

    private String contentType = "application/json";
}
