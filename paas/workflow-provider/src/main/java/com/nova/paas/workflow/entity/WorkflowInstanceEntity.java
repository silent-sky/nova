package com.nova.paas.workflow.entity;

import com.nova.paas.workflow.pojo.ExecutionPojo;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * zhenghaibo
 * 2018/5/16 10:48
 */
@Data
@Document(collection = "workflowInstances")
public class WorkflowInstanceEntity implements Serializable {
    private static final long serialVersionUID = -6337881785440004677L;

    @Id
    private ObjectId id;
    private String tenantId;
    private String appId;
    private String workflowId;
    private String objectId;
    private String entityId;
    private String applicantId;
    private String status;
    private String type;
    private Long start;
    private Long end;
    private Long duration;
    private Long modifiedAt;
    private String modifiedBy;
    private String sourceWorkflowId;
    private String workflowName;
    private String workflowDescription;
    private String triggerType;
    private Boolean deleted;
    private Map<String, List<ExecutionPojo>> execution;

}
