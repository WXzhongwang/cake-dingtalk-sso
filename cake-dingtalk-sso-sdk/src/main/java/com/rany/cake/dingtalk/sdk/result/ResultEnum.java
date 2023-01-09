package com.rany.cake.dingtalk.sdk.result;

/**
 * @author tutu
 */

public enum ResultEnum {
    SUCCESS(0,"success"),
    FAILE(-1,"faile"),
    REFRESH_TOKEN_ERROR(1001, "refreshToken错误或已失效");

    public int code;

    public String message;

    ResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
