package com.effektif.workflow.impl.ext;

import com.effektif.workflow.api.model.TaskId;

import java.util.List;

/**
 * zhenghaibo
 * 18/4/8 15:38
 */
public interface TaskStore {

    TaskId generateTaskId();

    void insertTask(Task task);

    List<Task> findTasks(UserTaskQuery taskQuery);

    Task assignTask(String taskId, List<String> assigneeIds);

    Task completeTask(String taskId, String actionType);

    Task completeTask(String taskId, String actionType, List<ApprovalOpinion> opinionList, List<String> assigneeIds);

    Task completeTask(String taskId, String actionType, String state, List<ApprovalOpinion> opinionList);

    Task updateApprovalOpinion(String taskId, List<ApprovalOpinion> opinionList, List<String> assigneeIds);

    void deleteTasks(UserTaskQuery taskQuery);
}
