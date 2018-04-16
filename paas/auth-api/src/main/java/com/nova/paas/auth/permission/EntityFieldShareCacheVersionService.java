package com.nova.paas.auth.permission;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.common.pojo.CommonContext;

import java.util.List;
import java.util.Map;
/**
 * zhenghaibo
 * 2018/4/13 17:19
 */
public interface EntityFieldShareCacheVersionService {

    Map<String, String> rulesCurrentVersion(
            CommonContext context, String entityId, List<String> rules) throws AuthServiceException;

    /**
     * 更新规则的当前版本
     */
    void updateRuleCurrentVersion(
            CommonContext context, String rule, String currentVersion) throws AuthServiceException;

    /**
     * 更新规则的最新版本
     */
    void updateRuleNewVersion(CommonContext context, String rule, String newVersion) throws AuthServiceException;

}
