package com.nova.paas.common.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = -7462766473712327815L;
    private int errCode; //错误码
    private String errMessage; //错误信息
    private T result;  //结果实体

    public Result() {
        this.errCode = 0;
        this.errMessage = "成功";
    }

    public Result(int errCode, String errMessage, T result) {
        this.errCode = errCode;
        this.errMessage = errMessage;
        this.result = result;
    }
}
