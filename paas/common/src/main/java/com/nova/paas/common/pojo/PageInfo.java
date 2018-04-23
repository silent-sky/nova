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
    //默认分页大小
    private static final int DEFAULT_PAGE_SIZE = 20;
    //默认当前页
    private static final int DEFAULT_CURRENT_PAGE = 1;

    //当前页码
    private int pageNum;
    //每页条数
    private int pageSize;
    //总页数
    private Integer totalPage;
    //总记录数
    private long total;
    //起始记录数
    private int start;
    //private String orderBy;
    //    private boolean asc;

    public PageInfo() {
        this.pageNum = DEFAULT_CURRENT_PAGE;
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    public PageInfo(int pageSize, int pageNum) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public int getStart() {
        return (pageNum - 1) * pageSize;
    }
}
