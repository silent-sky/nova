package com.nova.paas.auth.service.impl.permission;

import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.service.permission.EntityFieldShareCacheVersionService;
import com.nova.paas.common.pojo.CommonContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EntityFieldShareCacheVersionServiceImpl implements EntityFieldShareCacheVersionService {

    public Map<String, String> rulesCurrentVersion(
            CommonContext context, String entityId, List<String> rules) throws AuthServiceException {
        return null;
    }

    /**
     * 更新规则的当前版本
     */
    public void updateRuleCurrentVersion(
            CommonContext context, String rule, String currentVersion) throws AuthServiceException {

    }

    /**
     * 更新规则的最新版本
     */
    public void updateRuleNewVersion(
            CommonContext context, String rule, String newVersion) throws AuthServiceException {

    }

}
