package com.nova.paas.workflow.param;

import lombok.Data;
import org.bson.types.ObjectId;

import java.util.Set;

/**
 * zhenghaibo
 * 2018/5/9 20:42
 */
@Data
public class InstanceQueryParam extends QueryParam {
    private static final long serialVersionUID = -1171478091769279840L;

    private String tenantId;
    private String applicantId;
    private String entityId;
    private String objectId;
    private String workflowId;
    private String workflowInstanceId;
    private String status;
}
