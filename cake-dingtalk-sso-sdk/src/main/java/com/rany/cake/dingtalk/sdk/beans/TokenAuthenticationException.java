package com.rany.cake.dingtalk.sdk.beans;

public class TokenAuthenticationException extends RuntimeException  {

    private int code;

    private String message;

    public TokenAuthenticationException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
