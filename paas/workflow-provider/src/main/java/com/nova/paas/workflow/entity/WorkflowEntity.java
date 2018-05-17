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
 * 2018/5/16 10:42
 */
@Data
@Document(collection = "workflows")
public class WorkflowEntity implements Serializable {
    private static final long serialVersionUID = 1362206479128569240L;

    @Id
    protected ObjectId id;
    private String sourceWorkflowId;
    private String tenantId;
    private String entityId;
    private String objectId;
    private String appId;
    private String type;
    private String name;
    private String description;
    private Boolean enable;
    private Boolean deleted;
    private Long createdAt;
    private String createdBy;
    private Long modifiedAt;
    private String modifiedBy;
    private Integer priority;
    private Map<String, List<ExecutionPojo>> execution;
    private List<Map<String, Object>> activities;
}
