package com.nova.paas.workflow.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/5/9 20:36
 */
@Data
public class ActivityInstancePojo implements Serializable {

    private static final long serialVersionUID = 964497509499376759L;
    private String id;
    private String activityId;
    private Long start;
    private Long end;
    private Long duration;
    private String status;
    private String activityName;
}
