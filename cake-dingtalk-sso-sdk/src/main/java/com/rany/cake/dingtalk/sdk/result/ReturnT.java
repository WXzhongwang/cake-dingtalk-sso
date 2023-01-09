package com.rany.cake.dingtalk.sdk.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author tutu
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnT {


    private Integer code;

    private String msg;

    private Object data;

    public ReturnT(Object data) {
        this.data = data;
        this.code = ResultEnum.SUCCESS.code;
        this.msg = ResultEnum.SUCCESS.message;
    }

    public ReturnT(ResultEnum resultEnum) {
        this.code = resultEnum.SUCCESS.code;
        this.msg = resultEnum.SUCCESS.message;
    }

    public static ReturnT success(){
        return new ReturnT(ResultEnum.SUCCESS);
    }

    public static ReturnT success(Object data){
        return new ReturnT(data);
    }

    public static ReturnT failure(){
        return new ReturnT(ResultEnum.FAILE);
    }

    public static ReturnT failure(ResultEnum resultEnum) {
        return new ReturnT(resultEnum);
    }

    public static ReturnT failure(Object data){
        ReturnT returnT = failure();
        returnT.setData(data);
        return returnT;
    }

}
