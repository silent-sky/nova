package com.nova.paas.workflow.dao;

import com.effektif.workflow.mongo.WorkflowFields;
import com.nova.paas.workflow.entity.WorkflowEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

/**
 * zhenghaibo
 * 2018/5/16 14:11
 */
@Component
public class WorkflowDao {
    @Inject
    private MongoTemplate mongoTemplate;

    public boolean checkSourceWorkflowIdUnique(String tenantId, String sourceWorkflowId) {
        boolean isUnique = false;
        if (StringUtils.isNoneBlank(tenantId, sourceWorkflowId)) {

            Query query = new Query(Criteria.where(WorkflowFields.TENANT_ID).is(tenantId));
            query.addCriteria(Criteria.where(WorkflowFields.SOURCE_WORKFLOW_ID).is(sourceWorkflowId));
            query.addCriteria(Criteria.where(WorkflowFields.DELETED).exists(false));
            List<WorkflowEntity> list = mongoTemplate.find(query, WorkflowEntity.class);
            if (CollectionUtils.isEmpty(list)) {
                isUnique = true;
            }
        }
        return isUnique;
    }

    public WorkflowEntity getLatestWorkflowBySourceId(String tenantId, String sourceWorkflowId) {
        WorkflowEntity entity = null;
        Query query = new Query(Criteria.where(WorkflowFields.TENANT_ID).is(tenantId));
        query.addCriteria(Criteria.where(WorkflowFields.SOURCE_WORKFLOW_ID).is(sourceWorkflowId));
        query.addCriteria(Criteria.where(WorkflowFields.DELETED).exists(false));
        query.with(new Sort(Sort.Direction.DESC, WorkflowFields._ID));
        List<WorkflowEntity> list = mongoTemplate.find(query, WorkflowEntity.class);
        if (CollectionUtils.isNotEmpty(list)) {
            entity = list.get(0);
        }
        return entity;

    }
}
