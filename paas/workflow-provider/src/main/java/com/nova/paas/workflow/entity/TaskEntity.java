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
 * 2018/5/15 20:44
 */
@Data
@Document(collection = "tasks")
public class TaskEntity implements Serializable {
    private static final long serialVersionUID = 5429455307365961121L;

    @Id
    private ObjectId id;
    private String tenantId;
    private String appId;
    private String sourceWorkflowId;
    private String workflowId;
    private String workflowInstanceId;
    private String entityId;
    private String objectId;
    private String activityId;
    private String activityInstanceId;
    private Long createdAt;
    private Long modifiedAt;
    private String modifiedBy;
    private String applicantId;
    private String status;
    private String taskType;
    private String actionType;
    private Map<String, List<String>> assignee;
    private List<OpinionEntity> opinions;
    private Boolean remind;
    private Integer remindLatency;

    private List<String> assigneeIds;
    private Boolean completed;
    private Boolean canceled;
    private String workflowName;
    private String workflowDescription;
    private String name;
    private String description;

    private Boolean assigneeChanged;
    private List<RemindEntity> reminders;
    private Boolean deleted;
    private Map<String, List<ExecutionPojo>> execution;
    private RuleEntity rule;
    private Map<String, String> sourceTransition;

    /**
     * 会签时：
     * allPassType = 1 表示所有人会签人员都要进行一次操作，即使第一个人执行了reject，后面的人也要执行
     * allPassType = 0 如果第一个人执行了reject，则流程就终止，会签的其他人不需要执行。
     */
    private Integer allPassType;

    /**
     * 1:表示可以指定下一个审批人
     * 0或空：不可以
     */
    private Integer assignNextTask;

    /**
     * 1:表示该节点的审批人由上一节点指定
     * 0或空：不需要
     */
    private Integer candidateByPreTask;
}
