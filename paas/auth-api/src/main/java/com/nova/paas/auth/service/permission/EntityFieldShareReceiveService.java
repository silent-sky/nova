package com.nova.paas.auth.service.permission;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.common.pojo.CommonContext;

import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/13 17:19
 */
public interface EntityFieldShareReceiveService {

    void entityFieldShareRuleReceiveCacheReset(Comparable context, Set<String> rules) throws AuthServiceException;

    void entityFieldShareRuleReceiveCacheRemoveUsersFromRules(CommonContext context, Set<String> rules, Set<String> users)
            throws AuthServiceException;

    void entityFieldShareRuleReceiveCacheAddUsersToRules(CommonContext context, Set<String> rules, Set<String> users) throws AuthServiceException;

    void entityFieldShareRuleReceiveCacheDelRules(CommonContext context, Set<String> rules) throws AuthServiceException;

    void entityFieldShareRuleReceiveCacheDelUsers(CommonContext context, Set<String> users) throws AuthServiceException;

    void entityFieldShareRuleReceiveCachePermissionUpdate(CommonContext context, Set<String> rules, Integer permission) throws AuthServiceException;

}
