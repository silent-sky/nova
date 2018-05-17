package com.nova.paas.workflow.dao;

import com.effektif.workflow.mongo.WorkflowFields;
import com.nova.paas.workflow.constant.RuleFields;
import com.nova.paas.workflow.entity.RuleEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/5/16 16:44
 */
@Component
public class RuleDao {
    @Inject
    private MongoTemplate mongoTemplate;

    public String insert(RuleEntity entity) {
        entity.setCreatedAt(System.currentTimeMillis());
        entity.setModifiedAt(System.currentTimeMillis());
        entity.setDeleted(false);
        mongoTemplate.insert(entity);
        return entity.getId().toString();
    }

    public void update(RuleEntity entity) {
        Query query =
                new Query(Criteria.where(RuleFields.TENANT_ID).is(entity.getTenantId())).addCriteria(Criteria.where(RuleFields.DELETED).is(false));
        Update update = new Update().set(RuleFields.CONDITION_PATTERN, entity.getConditionPattern())
                .set(RuleFields.CONDITIONS, entity.getConditions())
                .set(WorkflowFields.CREATED_AT, System.currentTimeMillis())
                .set(WorkflowFields.CREATED_BY, entity.getCreatedBy());
        mongoTemplate.updateFirst(query, update, RuleEntity.class);
    }

    public void delete(String tenantId, String userId, Set<String> sourceWorkflowIds) {
        Query query = new Query(Criteria.where(RuleFields.TENANT_ID).is(tenantId)).addCriteria(Criteria.where(RuleFields.SOURCE_WORKFLOW_ID)
                .in(sourceWorkflowIds));

        Update update = new Update().set(RuleFields.MODIFIED_AT, System.currentTimeMillis())
                .set(RuleFields.MODIFIED_BY, userId)
                .set(RuleFields.DELETED, true);
        mongoTemplate.updateFirst(query, update, RuleEntity.class);
    }
}
