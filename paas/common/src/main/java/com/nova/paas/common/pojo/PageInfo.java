package com.nova.paas.common.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2017/1/3 14:37
 */
@Data
public class PageInfo implements Serializable {
    private static final long serialVersionUID = 704560271874494648L;

    private static final int DEFAULT_PAGE_SIZE = 10;  //默认分页大小
    private static final int DEFAULT_CURRENT_PAGE = 1; //默认当前页

    private int pageNum; //当前页数
    private int pageSize; //每页数量
    private Integer totalPage; //总页数
    private long total; //总记录数
    //private String orderBy;
    private boolean asc;

    public PageInfo() {
        this.pageNum = DEFAULT_CURRENT_PAGE;
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    public PageInfo(int pageSize, int pageNum) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
