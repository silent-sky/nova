package com.nova.paas.workflow.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/5/10 10:26
 */
@Data
public class OpinionPojo implements Serializable {
    private static final long serialVersionUID = 7699563911901394680L;

    private String tenantId;
    private String userId;
    //{"agree","reject","go_back"}
    private String actionType;
    private String opinion;
    private Long createdAt;
}
