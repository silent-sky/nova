package com.nova.paas.common.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * zhenghaibo
 * 2017/1/5 11:30
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 5758629387814290977L;
    private int errCode; //错误码
    private String errMessage; //错误信息
    private long totalRecord;
    private int pageNumber;
    private int pageSize;
    private List<T> result;

    public PageResult() {
        this.errCode = 0;
        this.errMessage = "成功";
    }

    public PageResult(int errCode, String errMessage, List<T> result) {
        this.errCode = errCode;
        this.errMessage = errMessage;
        this.result = result;
    }
}
