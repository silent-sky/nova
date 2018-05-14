package com.nova.paas.workflow.param;

import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/5/9 20:42
 */
@Data
public class QueryParam implements Serializable {
    private static final long serialVersionUID = 4218643825036393962L;

    private Integer skip;
    private Integer limit;
    private String orderBy;
}
