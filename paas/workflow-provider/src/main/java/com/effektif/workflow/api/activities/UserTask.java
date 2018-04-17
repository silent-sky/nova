package com.effektif.workflow.api.activities;

import com.effektif.workflow.api.bpmn.BpmnElement;
import com.effektif.workflow.api.ext.ExecutionPojo;
import com.effektif.workflow.api.json.TypeName;
import com.effektif.workflow.api.workflow.Activity;

import java.util.List;
import java.util.Map;

/**
 * zhenghaibo
 * 18/4/8 15:09
 */
@TypeName("userTask")
@BpmnElement("userTask")
public class UserTask extends Activity {
    private static final long serialVersionUID = 8809813461884835903L;
    /**
     * 审批人,key 为 assigneeType
     */
    protected Map<String, List<String>> assignee;
    /**
     * 指派类型
     *
     * @see com.effektif.workflow.api.ext.WorkflowConstants.AssigneeType
     */
    protected String assignType;
    /**
     * 审批任务类型
     *
     * @see com.effektif.workflow.api.ext.WorkflowConstants.UserTaskType
     */
    protected String taskType;
    protected Map<String, List<ExecutionPojo>> execution;
    protected Object rule;
    //设置是否为审批例外人
    protected Boolean candidateEditable;

    //任务的预期截止时间
    protected Long dueDate;
    //发送催办提醒的时间
    protected Long reminder;
    //提醒周期间隔
    protected Long reminderRepeat;
    //是否允许任务执行人将任务转给其他人处理
    protected Boolean allowEscalate;
    //是否需要上一级审批
    protected Boolean demandSuperior;

    //0：上级审批；1：流程终止；2：指定审批人
    protected Integer demandBeyondAssignee;


    /**
     * 1:表示可以指定下一个审批人
     * 0或空：不可以
     */

    private Integer assignNextTask;

    /**
     * 1:是外部节点
     * 0或空：不是外部节点
     */

    private Integer externalApplyTask;

    /**
     * 1:表示该节点的审批人由上一节点指定
     * 0或空：不需要
     */

    private Integer candidateByPreTask;


    public Integer getAllPassType() {
        return allPassType;
    }

    public void setAllPassType(Integer allPassType) {
        this.allPassType = allPassType;
    }

    /**
     *  会签时：
     *  allPassType = 1 表示所有人会签人员都要进行一次操作，即使第一个人执行了reject，后面的人也要执行
     *  allPassType = 0 如果第一个人执行了reject，则流程就终止，会签的其他人不需要执行。
     */

    private Integer allPassType;


    /**
     *  针对demandBeyondAssignee==2的情况     */

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

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public UserTask dueDate(Long dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public Long getReminder() {
        return reminder;
    }

    public void setReminder(Long reminder) {
        this.reminder = reminder;
    }

    public Long getReminderRepeat() {
        return reminderRepeat;
    }

    public void setReminderRepeat(Long reminderRepeat) {
        this.reminderRepeat = reminderRepeat;
    }

    public Boolean getAllowEscalate() {
        return allowEscalate;
    }

    public void setAllowEscalate(Boolean allowEscalate) {
        this.allowEscalate = allowEscalate;
    }

    public UserTask allowEscalate(Boolean allowEscalate) {
        this.allowEscalate = allowEscalate;
        return this;
    }

    public String getAssignType() {
        return assignType;
    }

    public void setAssignType(String assignType) {
        this.assignType = assignType;
    }

    public UserTask assignType(String assignType) {
        this.assignType = assignType;
        return this;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public UserTask taskType(String taskType) {
        this.taskType = taskType;
        return this;
    }

    public Map<String, List<String>> getAssignee() {
        return assignee;
    }

    public void setAssignee(Map<String, List<String>> assignee) {
        this.assignee = assignee;
    }

    public UserTask assignee(Map<String, List<String>> assignee) {
        this.assignee = assignee;
        return this;
    }

    public Map<String, List<ExecutionPojo>> getExecution() {
        return execution;
    }

    public void setExecution(Map<String, List<ExecutionPojo>> execution) {
        this.execution = execution;
    }

    public Object getRule() {
        return rule;
    }

    public void setRule(Object rule) {
        this.rule = rule;
    }

    public Boolean getCandidateEditable() {
        return candidateEditable;
    }

    public void setCandidateEditable(Boolean candidateEditable) {
        this.candidateEditable = candidateEditable;
    }

    public Boolean getDemandSuperior() {
        return demandSuperior;
    }

    public void setDemandSuperior(Boolean demandSuperior) {
        this.demandSuperior = demandSuperior;
    }

    public Integer getAssignNextTask() {
        return assignNextTask;
    }

    public void setAssignNextTask(Integer assignNextTask) {
        this.assignNextTask = assignNextTask;
    }

    public Integer getExternalApplyTask() {
        return externalApplyTask;
    }

    public void setExternalApplyTask(Integer externalApplyTask) {
        this.externalApplyTask = externalApplyTask;
    }

    public Integer getCandidateByPreTask() {
        return candidateByPreTask;
    }

    public void setCandidateByPreTask(Integer candidateByPreTask) {
        this.candidateByPreTask = candidateByPreTask;
    }
}
