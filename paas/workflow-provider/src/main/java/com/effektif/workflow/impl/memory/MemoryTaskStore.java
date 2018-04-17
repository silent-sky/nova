package com.effektif.workflow.impl.memory;

import com.effektif.workflow.api.model.Id;
import com.effektif.workflow.api.model.TaskId;
import com.effektif.workflow.impl.ext.ApprovalOpinion;
import com.effektif.workflow.impl.ext.Task;
import com.effektif.workflow.impl.ext.TaskStore;
import com.effektif.workflow.impl.ext.UserTaskQuery;

import java.util.*;

/**
 * zhenghaibo
 * 18/4/8 10:18
 */
public class MemoryTaskStore implements TaskStore {

    protected int nextId = 1;
    protected Map<Id, Task> tasks = Collections.synchronizedMap(new LinkedHashMap<Id, Task>());

    @Override
    public TaskId generateTaskId() {
        return new TaskId(Integer.toString(nextId++));
    }

    @Override
    public void insertTask(Task task) {
        if (task.getId() == null) {
            String taskId = Integer.toString(nextId++);
            task.setId(new TaskId(taskId));
        }
        task.setModifyTime(System.currentTimeMillis());
        tasks.put(task.getId(), task);
    }

    @Override
    public List<Task> findTasks(UserTaskQuery query) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (query == null) {
                result.add(task);
            }
        }
        return result;
    }

    @Override
    public void deleteTasks(UserTaskQuery taskQuery) {
        tasks.clear();
    }

    @Override
    public Task assignTask(String taskId, List<String> assigneeIds) {
        Task task = tasks.get(taskId);
        if (task != null) {
            task.setAssigneeIds(assigneeIds);
            task.setModifyTime(System.currentTimeMillis());
        }
        return task;
    }

    @Override
    public Task completeTask(String taskId, String actionType) {
        return findTaskById(taskId);
    }

    @Override public Task completeTask(String taskId, String actionType, List<ApprovalOpinion> opinionList, List<String> assigneeIds) {
        return findTaskById(taskId);
    }

    @Override
    public Task completeTask(String taskId, String actionType, String state, List<ApprovalOpinion> opinionList) {
        return findTaskById(taskId);
    }

    @Override public Task updateApprovalOpinion(String taskId, List<ApprovalOpinion> opinionList, List<String> assigneeIds) {
        return findTaskById(taskId);
    }


    public Task findTaskById(String taskId) {
        return tasks.get(taskId);
    }
}
