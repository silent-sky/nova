package com.nova.paas.workflow.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/5/16 10:36
 */
@Data
public class OpinionEntity implements Serializable {
    private static final long serialVersionUID = -3771729231800733012L;

    private String tenantId;
    private String userId;
    private String actionType;
    private String opinion;
    private Long replyTime;
}
