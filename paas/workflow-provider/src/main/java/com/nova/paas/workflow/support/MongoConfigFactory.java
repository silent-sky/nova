package com.nova.paas.workflow.support;

import com.effektif.workflow.api.WorkflowEngine;
import com.effektif.workflow.impl.WorkflowEngineImpl;
import com.effektif.workflow.impl.ext.TaskFactory;
import com.effektif.workflow.mongo.DefaultMongoObjectMapper;
import com.effektif.workflow.mongo.MongoConfiguration;
import com.effektif.workflow.mongo.MongoObjectMapper;
import com.effektif.workflow.mongo.MongoWorkflowStore;
import lombok.extern.slf4j.Slf4j;

/**
 * zhenghaibo
 * 2018/5/16 13:59
 */
@Slf4j
public class MongoConfigFactory {

    private static MongoConfigFactory instance = new MongoConfigFactory();
    private MongoConfiguration mongoConfiguration;
    private WorkflowEngine engine;
    private TaskFactory taskFactory;
    private MongoWorkflowStore mongoWorkflowStore;
    private MongoObjectMapper mongoObjectMapper;

    private MongoConfigFactory() {
        super();
        init();
    }

    public static MongoConfigFactory getInstance() {
        return instance;
    }

    private void init() {
        mongoConfiguration = new MongoConfiguration();
        engine = mongoConfiguration.getWorkflowEngine();
        //        if (engine instanceof WorkflowEngineImpl) {
        //            ((WorkflowEngineImpl) engine).addWorkflowExecutionListener(new WfExecuteListener());
        //            ((WorkflowEngineImpl) engine).addWorkflowExecutionListener(new BPMExecuteListener());
        //        }
        taskFactory = mongoConfiguration.get(TaskFactory.class);
        mongoWorkflowStore = mongoConfiguration.get(MongoWorkflowStore.class);
        mongoObjectMapper = new DefaultMongoObjectMapper();

    }

    public WorkflowEngine getEngine() {
        return engine;
    }

    public TaskFactory getTaskFactory() {
        return taskFactory;
    }

    public MongoWorkflowStore getMongoWorkflowStore() {
        return mongoWorkflowStore;
    }

    public MongoObjectMapper getMongoObjectMapper() {
        return mongoObjectMapper;
    }

}
