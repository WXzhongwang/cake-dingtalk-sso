package com.rany.cake.dingtalk.sdk.beans;

import lombok.*;

import java.io.Serializable;

/**
 * @author tutu
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoUser implements Serializable {

    private String userId;

    private String userName;

    private String realName;
}
