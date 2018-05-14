package com.effektif.workflow.mongo;

import com.effektif.workflow.api.Configuration;
import com.effektif.workflow.api.model.TaskId;
import com.effektif.workflow.api.query.OrderBy;
import com.effektif.workflow.api.query.OrderDirection;
import com.effektif.workflow.impl.WorkflowEngineImpl;
import com.effektif.workflow.impl.activity.ActivityTypeService;
import com.effektif.workflow.impl.configuration.Brewable;
import com.effektif.workflow.impl.configuration.Brewery;
import com.effektif.workflow.impl.ext.ApprovalOpinion;
import com.effektif.workflow.impl.ext.Task;
import com.effektif.workflow.impl.ext.TaskStore;
import com.effektif.workflow.impl.ext.UserTaskQuery;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * zhenghaibo
 * 18/4/8 15:55
 */
public class MongoTaskStore implements TaskStore, Brewable {
    public static final Logger log = MongoDb.log;

    protected WorkflowEngineImpl workflowEngine;
    protected MongoCollection tasksCollection;
    protected ActivityTypeService activityTypeService;
    protected Configuration configuration;
    protected MongoObjectMapper mongoMapper;

    @Override public void brew(Brewery brewery) {
        MongoDb mongoDb = brewery.get(MongoDb.class);
        MongoConfiguration mongoConfiguration = brewery.get(MongoConfiguration.class);
        this.tasksCollection =
            mongoDb.createCollection(mongoConfiguration.getTasksCollectionName());
        this.configuration = brewery.get(Configuration.class);
        this.workflowEngine = brewery.get(WorkflowEngineImpl.class);
        this.activityTypeService = brewery.get(ActivityTypeService.class);
        this.mongoMapper = brewery.get(MongoObjectMapper.class);
    }

    public BasicDBObject taskToMongo(Task task) {
        return (BasicDBObject) mongoMapper.write(task);
    }

    public <T extends Task> T mongoToTask(BasicDBObject dbTask) {
        return mongoMapper.read(dbTask, Task.class);
    }

    @Override public TaskId generateTaskId() {
        return new TaskId(new ObjectId().toString());
    }

    @Override public void insertTask(Task task) {
        task.setUpdatedAt(System.currentTimeMillis());
        BasicDBObject dbTask = taskToMongo(task);
        tasksCollection.insert("insert-task", dbTask);
    }

    @Override public List<Task> findTasks(UserTaskQuery taskQuery) {
        List<Task> tasks = new ArrayList<>();
        BasicDBObject dbQuery = createDbQuery(taskQuery).get();
        DBCursor dbCursor = tasksCollection.find("find-tasks", dbQuery);
        if (taskQuery.getLimit() != null) {
            dbCursor.limit(taskQuery.getLimit());
        }
        if (taskQuery.getOrderBy() != null) {
            dbCursor.sort(writeOrderBy(taskQuery.getOrderBy()));
        }
        while (dbCursor.hasNext()) {
            BasicDBObject dbTask = (BasicDBObject) dbCursor.next();
            Task task = mongoToTask(dbTask);
            tasks.add(task);
        }
        return tasks;
    }

    @Override public Task assignTask(String taskId, List<String> assigneeIds) {
        BasicDBObject query = new Query()
            ._id(taskId)
            .get();
        BasicDBObject update = new Update()
            .set(TaskFields.ASSIGNEE_IDS, assigneeIds)
            .set(TaskFields.MODIFY_TIME, System.currentTimeMillis())
            .get();
        BasicDBObject dbTask = tasksCollection.findAndModify("assign-task", query, update);
        return mongoToTask(dbTask);
    }

    @Override public Task completeTask(String taskId, String actionType) {
        BasicDBObject query = new Query()
            ._id(taskId)
            .get();
        BasicDBObject update = new Update()
            .set(TaskFields.COMPLETED, true)
            .set(TaskFields.MODIFY_TIME, System.currentTimeMillis())
            .set(TaskFields.ACTION_TYPE, actionType)
            .get();
        // this findAndModify returns the old version
        BasicDBObject dbTask = tasksCollection
            .findAndModify("complete-task", query, update, null, null, false, false, false);
        return mongoToTask(dbTask);
    }

    @Override public Task completeTask(String taskId, String actionType, List<ApprovalOpinion> opinionList, List<String> assigneeIds) {
        //TODO 考虑并发，查询的时候应该加上modifyTime
        BasicDBObject query = new Query()
            ._id(taskId)
            .get();
        BasicDBList opinionListObj = new BasicDBList();
        for (ApprovalOpinion opinion : opinionList) {
            opinionListObj.add(new BasicDBObject()
                .append("tenantId", opinion.getTenantId())
                .append("replyTime", opinion.getReplyTime())
                .append("userId", opinion.getUserId())
                .append("actionType", opinion.getActionType())
                .append("opinion", opinion.getOpinion()));
        }
        BasicDBObject update = new Update()
            .set(TaskFields.COMPLETED, true)
            .set(TaskFields.ASSIGNEE_IDS, assigneeIds)
            .set(TaskFields.MODIFY_TIME, System.currentTimeMillis())
            .set(TaskFields.ACTION_TYPE, actionType)
            .set(TaskFields.OPINIONS, opinionListObj)
            .get();
        // this findAndModify returns the old version
        BasicDBObject dbTask = tasksCollection
            .findAndModify("complete-task", query, update, null, null, false, false, false);
        return mongoToTask(dbTask);
    }

    @Override
    public Task completeTask(String taskId, String actionType, String state, List<ApprovalOpinion> opinionList) {
        BasicDBObject query = new Query()
            ._id(taskId)
            .get();
        BasicDBList opinionListObj = new BasicDBList();
        for (ApprovalOpinion opinion : opinionList) {
            opinionListObj.add(new BasicDBObject()
                .append("tenantId", opinion.getTenantId())
                .append("replyTime", opinion.getReplyTime())
                .append("userId", opinion.getUserId())
                .append("actionType", opinion.getActionType())
                .append("opinion", opinion.getOpinion()));
        }
        BasicDBObject update = new Update()
            .set(TaskFields.COMPLETED, true)
            .set(TaskFields.MODIFY_TIME, System.currentTimeMillis())
            .set(TaskFields.ACTION_TYPE, actionType)
            .set(TaskFields.STATE, state)
            .set(TaskFields.OPINIONS, opinionListObj)
            .get();
        // this findAndModify returns the old version
        BasicDBObject dbTask = tasksCollection
            .findAndModify("complete-task", query, update, null, null, false, false, false);
        return mongoToTask(dbTask);
    }

    @Override public Task updateApprovalOpinion(String taskId, List<ApprovalOpinion> opinionList, List<String> assigneeIds) {
        BasicDBObject query = new Query()
            ._id(taskId)
            .get();
        BasicDBList opinionListObj = new BasicDBList();
        for (ApprovalOpinion opinion : opinionList) {
            opinionListObj.add(new BasicDBObject()
                .append("tenantId", opinion.getTenantId())
                .append("replyTime", opinion.getReplyTime())
                .append("userId", opinion.getUserId())
                .append("actionType", opinion.getActionType())
                .append("opinion", opinion.getOpinion()));
        }
        BasicDBObject update = new Update()
            .set(TaskFields.ASSIGNEE_IDS, assigneeIds)
            .set(TaskFields.MODIFY_TIME, System.currentTimeMillis())
            .set(TaskFields.OPINIONS, opinionListObj)
            .get();
        BasicDBObject dbTask = tasksCollection.findAndModify("update-task-opinion", query, update, null, null, false, false, false);
        return mongoToTask(dbTask);
    }

    @Override public void deleteTasks(UserTaskQuery taskQuery) {
        BasicDBObject dbQuery = createDbQuery(taskQuery).get();
        tasksCollection.remove("delete-tasks", dbQuery);
    }

    private Query createDbQuery(UserTaskQuery taskQuery) {
        if (taskQuery == null) {
            taskQuery = new UserTaskQuery();
        }
        Query mongoQuery = new Query();

        if (taskQuery.getTaskId() != null) {
            mongoQuery.equal(TaskFields._ID, new ObjectId(taskQuery.getTaskId()));
        }
        if (taskQuery.getTaskName() != null) {
            mongoQuery.equal(TaskFields.NAME, Pattern.compile(taskQuery.getTaskName()));
        }
        if (taskQuery.getCompleted() != null) {
            if (taskQuery.getCompleted()) {
                mongoQuery.equal(TaskFields.COMPLETED, true);
            } else {
                mongoQuery.doesNotExist(TaskFields.COMPLETED);
            }
        }
        if (taskQuery.getWorkflowInstanceId() != null) {
            mongoQuery.equal(TaskFields.WORKFLOW_INSTANCE_ID,
                taskQuery.getWorkflowInstanceId());
        }
        if (taskQuery.getApplicantId() != null) {
            mongoQuery.equal(TaskFields.APPLICANT_ID, taskQuery.getApplicantId());
        }
        if (taskQuery.getAssigneeId() != null) {
            mongoQuery.in(TaskFields.ASSIGNEE_IDS, taskQuery.getAssigneeIds());
        }
        if (taskQuery.getTenantId() != null) {
            mongoQuery.equal(TaskFields.TENANT_ID, taskQuery.getTenantId());
        }
        if (taskQuery.getAppId() != null) {
            mongoQuery.equal(TaskFields.APP_ID, taskQuery.getAppId());
        }
        if (taskQuery.getEntityId() != null) {
            mongoQuery.equal(TaskFields.ENTITY_ID, taskQuery.getEntityId());
        }
        if (taskQuery.getObjectId() != null) {
            mongoQuery.equal(TaskFields.OBJECT_ID, taskQuery.getObjectId());
        }
        return mongoQuery;
    }

    private DBObject writeOrderBy(List<OrderBy> orderBy) {
        BasicDBObject dbOrderBy = new BasicDBObject();
        for (OrderBy element : orderBy) {
            String dbField = element.getField();
            int dbDirection = (element.getDirection() == OrderDirection.asc ? 1 : -1);
            dbOrderBy.append(dbField, dbDirection);
        }
        return dbOrderBy;
    }
}
