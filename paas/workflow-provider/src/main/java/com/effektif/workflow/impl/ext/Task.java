package com.effektif.workflow.impl.ext;

import com.nova.paas.workflow.constant.WorkflowConstant;
import com.nova.paas.workflow.pojo.ExecutionPojo;
import com.effektif.workflow.api.model.TaskId;
import com.effektif.workflow.api.workflow.Extensible;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * zhenghaibo
 * 18/4/8 14:36
 */
@Data
public class Task extends Extensible implements Serializable {
    private static final long serialVersionUID = -237118055514253051L;
    protected TaskId id;
    protected String name;
    protected String description;
    protected String applicantId;
    protected Long createdAt;
    protected Long updatedAt;
    protected List<String> assigneeIds;
    protected List<String> candidateIds;
    //是否在实例中更换过审批人
    protected Boolean assigneeChanged;
    protected Long dueDate;
    protected Boolean completed;
    protected Boolean canceled;

    protected String activityId;
    protected String activityInstanceId;
    protected Boolean activityNotify;
    protected String workflowInstanceId;
    protected String sourceWorkflowId;
    protected String workflowId;

    protected String tenantId;
    protected String appId;
    protected String entityId;
    protected String objectId;
    protected String deptId;
    protected Map<String, List<String>> assignee;
    protected List<ApprovalOpinion> opinions;
    protected String actionType;

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

    /**
     * 审批人员类别
     *
     * @see WorkflowConstant.AssigneeType
     */
    protected String assignType;
    /**
     * task审批类型
     *
     * @see WorkflowConstant.UserTaskType
     */
    protected String taskType;
    protected String status;
    protected Boolean remind;
    protected Integer remindLatency;

    protected String workflowName;
    protected String workflowDescription;
    protected String errMsg;
    //task提醒
    protected Object reminders;
    //是否需要上一级审批
    protected Boolean demandSuperior;

    //0：上级审批；1：流程终止；2：指定审批人
    protected Integer demandBeyondAssignee;

    public Integer getAllPassType() {
        return allPassType;
    }

    public void setAllPassType(Integer allPassType) {
        this.allPassType = allPassType;
    }

    /**
     * 会签时：
     * allPassType = 1 表示所有人会签人员都要进行一次操作，即使第一个人执行了reject，后面的人也要执行
     * allPassType = 0 如果第一个人执行了reject，则流程就终止，会签的其他人不需要执行。
     */

    private Integer allPassType;

    /**
     * 针对demandBeyondAssignee==2的情况
     */

    protected Map<String, List<String>> beyondAssignee;

    public Map<String, List<String>> getBeyondAssignee() {
        return beyondAssignee;
    }

    public void setBeyondAssignee(Map<String, List<String>> beyondAssignee) {
        this.beyondAssignee = beyondAssignee;
    }

    public Integer getDemandBeyondAssignee() {
        return demandBeyondAssignee;
    }

    public void setDemandBeyondAssignee(Integer demandBeyondAssignee) {
        this.demandBeyondAssignee = demandBeyondAssignee;
    }

    protected Map<String, List<ExecutionPojo>> execution;
    protected Object rule;
    protected Map sourceTransition;
}
