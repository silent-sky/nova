package com.nova.paas.workflow.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.effektif.workflow.mongo.WorkflowFields;
import com.nova.paas.workflow.exception.WorkflowErrorMsg;
import com.nova.paas.workflow.exception.WorkflowException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/5/10 18:12
 */
public class WorkflowJsonCheckUtil {
    private static final Set<String> PROPERTIES_INCLUDE = new HashSet<>();
    private static final Set<String> PROPERTIES_REQUIRE = new HashSet<>();

    static {
        PROPERTIES_INCLUDE.add(WorkflowFields._ID);
        PROPERTIES_INCLUDE.add(WorkflowFields.SOURCE_WORKFLOW_ID);
        PROPERTIES_INCLUDE.add(WorkflowFields.TENANT_ID);
        PROPERTIES_INCLUDE.add(WorkflowFields.APP_ID);
        PROPERTIES_INCLUDE.add(WorkflowFields.TYPE);
        PROPERTIES_INCLUDE.add(WorkflowFields.ENTITY_ID);
        PROPERTIES_INCLUDE.add(WorkflowFields.NAME);
        PROPERTIES_INCLUDE.add(WorkflowFields.DESC);
        PROPERTIES_INCLUDE.add(WorkflowFields.REMIND);
        PROPERTIES_INCLUDE.add(WorkflowFields.REMIND_LATENCY);
        PROPERTIES_INCLUDE.add(WorkflowFields.VARIABLES);
        PROPERTIES_INCLUDE.add(WorkflowFields.ACTIVITIES);
        PROPERTIES_INCLUDE.add(WorkflowFields.TRANSITIONS);
        PROPERTIES_INCLUDE.add(WorkflowFields.PRIORITY);
        PROPERTIES_INCLUDE.add(WorkflowFields.EXECUTION);
        PROPERTIES_INCLUDE.add(WorkflowFields.TRIGGER_TYPES);
        PROPERTIES_INCLUDE.add(WorkflowFields.ENABLE);

        PROPERTIES_REQUIRE.add(WorkflowFields.TYPE);
        PROPERTIES_REQUIRE.add(WorkflowFields.ACTIVITIES);
        PROPERTIES_REQUIRE.add(WorkflowFields.TRANSITIONS);
    }

    public static String checkProperties(String workflowJson) {
        if (StringUtils.isBlank(workflowJson)) {
            throw new WorkflowException(WorkflowErrorMsg.PAAS_WF_DEFAULT_EXCEPTION);
        }
        JSONObject jsonObject = (JSONObject) JSON.parse(workflowJson);

        for (String key : PROPERTIES_REQUIRE) {
            if (!jsonObject.containsKey(key)) {
                throw new WorkflowException(WorkflowErrorMsg.PAAS_WF_DEFAULT_EXCEPTION);
            }
        }

        JSONObject retJSONObject = new JSONObject();

        retJSONObject.putAll(jsonObject);

        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            if (!PROPERTIES_INCLUDE.contains(entry.getKey())) {
                retJSONObject.remove(entry.getKey());
            }
        }
        return retJSONObject.toJSONString();
    }

    public static void checkPropertiesRequire(String workflowJson) {
        if (StringUtils.isBlank(workflowJson)) {
            throw new WorkflowException(WorkflowErrorMsg.PAAS_WF_DEFAULT_EXCEPTION);
        }
        JSONObject jsonObject = (JSONObject) JSON.parse(workflowJson);
        for (String key : PROPERTIES_REQUIRE) {
            if (!jsonObject.containsKey(key)) {
                throw new WorkflowException(WorkflowErrorMsg.PAAS_WF_DEFAULT_EXCEPTION);
            }
        }
    }
}
