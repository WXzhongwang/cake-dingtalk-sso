package com.rany.cake.dingtalk.sdk.beans;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author tutu
 */
@Setter
@Getter
@Builder
public class AjaxResponse implements Serializable {

    private Integer code;

    private String msg;

    private String loginUrl;
}
