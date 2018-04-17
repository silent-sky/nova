package com.effektif.workflow.api.ext;

/**
 * zhenghaibo
 * 2017/4/8 13:24
 */
public interface WorkflowConstants {

    /**
     * 一个流程实例中允许的最大activity instance数量,避免节点死循环
     */
    int MAX_ACTIVITY_INSTANCES = 199;


    interface InstanceStatus {
        String IN_PROGRESS = "in_progress";
        String PASS = "pass";
        String REJECT = "reject";
        String CANCEL = "cancel";
        String ERROR = "error";
    }


    interface UserTaskStatus {
        String IN_PROGRESS = "in_progress";
        String PASS = "pass";
        String AUTO_PASS = "auto_pass";
        String REJECT = "reject";
        String CANCEL = "cancel";
        String GO_BACK = "go_back";
        String AUTO_GO_BACK = "auto_go_back";
        String SCHEDULE = "schedule";
        String ERROR = "error";
    }


    interface UserTaskType {
        String SINGLE = "single";
        String ONE_PASS = "one_pass";
        String ALL_PASS = "all_pass";

        /* 引擎自身只处理以下组合 */
        String ONE = "one";//[person,applicant,dept_leader,leader,ext_bpm]
        String ANYONE = "anyone";//[person,dept,group,role,ext_bpm]
        String ONE_BY_ONE = "one_by_one";//[person,level,grade,ext_bpm]
        String ALL = "all";//[person,group,ext_bpm]
    }


    interface AssigneeType {
        String PERSON = "person";
        String DEPT = "dept";
        String GROUP = "group";
        String ROLE = "role";
        String APPLICANT = "applicant";
        String DEPT_LEADER = "dept_leader";
        String SUPERVISOR = "supervisor";
        String LEVEL = "level";
        String GRADE = "grade";
        String LEADER_LEVEL = "leader_level";
        String EXT_BPM = "ext_bpm";
        String extUserType = "extUserType";
        String ext_process = "ext_process";
    }


    interface ExecuteType {
        String SEND_EMAIL = "send_email";
        //发给crm的提醒
        String SEND_QIXIN = "send_qixin";
        //将定义时保存的一段json透传给crm做字段更新,过程中不解析json结构
        String UPDATES = "updates";
        String TRIGGER_BPM = "trigger_bpm";
        String TRIGGER_OPERATION = "trigger_operation";

        String CREATE_ENTITY = "create_entity";
        String MODIFY_ENTITY = "modify_entity";
        String WEB_HOOK = "web_hook";
    }


    interface Action {
        String AGREE = "agree";
        String AUTO_AGREE = "auto_agree";
        String REJECT = "reject";
        String GO_BACK = "go_back";
        String AUTO_GO_BACK = "auto_go_back";
        String CANCEL = "cancel";
    }


    interface SystemVariable {
        //审批操作
        String DECIDE = "action";
        //最近的一次审批节点id
        String LATEST_USER_TASK_ID = "latest_user_task_id";
        //执行auto_go_back的标识
        String AUTO_GO_BACK_FLAG="auto_go_back_flag";
    }


    interface WorkflowType {
        String APPROVAL_FLOW = "approvalflow";
        String WORKFLOW = "workflow";
        String BPM = "workflow_bpm";
    }


    interface AppId {
        String APP_ID_XT = "facishare-xt";
        String APP_ID_CRM = "CRM";
        String APP_ID_BPM = "BPM";
    }


    interface ActivityType {
        String USER_TASK = "user_task";
        String EXCLUSIVE_GATEWAY = "exclusive_gateway";
        String PARALLEL_GATEWAY = "parallel_gateway";
        String EXECUTION_TASK = "execution_task";
    }


    interface GatewayType {
        //默认独占网关
        int DEFAULT = 0;
        //用户设置的独占网关
        int CONDITIONAL = 1;
    }
}
