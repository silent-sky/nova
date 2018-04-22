package com.nova.paas.auth.service.impl.permission;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nova.paas.auth.entity.permission.EntityFieldShareCache;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.permission.EntityFieldShareCacheMapper;
import com.nova.paas.auth.service.permission.EntityFieldShareCacheService;
import com.nova.paas.auth.service.permission.EntityFieldShareCacheVersionService;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class EntityFieldShareCacheServiceImpl implements EntityFieldShareCacheService {

    @Autowired
    private EntityFieldShareCacheMapper entityFieldShareCacheMapper;

    //    @Autowired
    //    private RuleGroupService ruleGroupService;

    @Autowired
    private EntityFieldShareCacheVersionService entityFieldShareCacheVersionService;

    @Transactional
    @Override
    public void updateDataVersions(
            CommonContext context, String entityId, String dataId, List<String> versions) throws AuthServiceException {
        if (StringUtils.isAnyBlank(entityId, dataId)) {
            return;
        }
        entityFieldShareCacheMapper.updateDataRules(context.getTenantId(), context.getAppId(), entityId, dataId, versions);
    }

    @Transactional
    @Override
    public void addDataVersions(
            CommonContext context, String entityId, String dataId, List<String> versions) throws AuthServiceException {
        if (StringUtils.isAnyBlank(entityId, dataId) || CollectionUtils.isEmpty(versions)) {
            return;
        }
    }

    /**
     * 查询数据规则
     */
    @Override
    public List<String> dataVersions(
            CommonContext context, String entityId, String dataId) throws AuthServiceException {
        if (StringUtils.isAnyBlank(entityId, dataId)) {
            return Lists.newArrayList();
        }
        return entityFieldShareCacheMapper.dataVersion(context.getTenantId(), context.getAppId(), entityId, dataId);
    }

    /**
     * 删除版本的数据
     */
    @Override
    public void deleteVersion(CommonContext context, String entityId, String version) throws AuthServiceException {
        if (!StringUtils.isAnyBlank(entityId, version)) {
            return;
        }
        //todo 考虑先查询后删除,避免死锁/不加事务,失败重新尝试
        entityFieldShareCacheMapper.deleteVersion(context.getTenantId(), context.getAppId(), entityId, version);
    }

    /**
     * 数据中添加规则
     */
    @Transactional
    @Override
    public void datasAddVersion(
            CommonContext context, String entityId, String version, List<String> dataIds) throws AuthServiceException {
        if (CollectionUtils.isEmpty(dataIds) || StringUtils.isAnyBlank(entityId, version)) {
            return;
        }
        /**
         * 先查询,如果没有data就需要添加
         */
        List<EntityFieldShareCache> entityFieldShareCacheList = new LinkedList<>();
        List<String> datas = new LinkedList<>();
        Set<String> existsDataIds = entityFieldShareCacheMapper.queryDataIds(context.getTenantId(), context.getAppId(), entityId, dataIds);
        if (existsDataIds.size() != dataIds.size()) {
            dataIds.forEach(dataId -> {
                if (!existsDataIds.contains(dataId)) {
                    EntityFieldShareCache cache = EntityFieldShareCache.builder()
                            .tenantId(context.getTenantId())
                            .entityId(entityId)
                            .dataId(dataId)
                            .rules(Lists.newArrayList(version))
                            .build();
                    entityFieldShareCacheList.add(cache);
                } else {
                    datas.add(dataId);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(entityFieldShareCacheList)) {
            //            entityFieldShareCacheMapper.batchInsert(entityFieldShareCacheList);
        }
        if (CollectionUtils.isNotEmpty(datas)) {
            entityFieldShareCacheMapper.datasAddRule(context.getTenantId(), context.getAppId(), entityId, version, dataIds);

        }
    }

    /**
     * 计算规则缓存
     */
    @Override
    public void calculateRuleCache(CommonContext context, String entityId, String ruleCode) throws AuthServiceException {
    /*
     *
     * 1. 生成新的version,更新version表的newVersion,存储version的expression,idGenterbal
     * 2. 调用规则引擎分页查询数据列表pageSize:50,直到没有数据(注意需要根据total,每页的条数,评估大概的页数避免一直取,死循环)
     *    1) 事务保证每插入50条缓存数据,如果失败直接重试,重试总时间为30mins(数据库异常运维处理时间)
     * 3. 完成缓存更新后,更新版本将新版本更新为当前版本
     * 4. 删除历史版本的expression
     *
     */
        //生成新版本号
        String newVersion = IdUtil.generateId();

        //更新新版本号
        entityFieldShareCacheVersionService.updateRuleNewVersion(context, ruleCode, newVersion);

        //取规则匹配到的数据
        boolean continueLoop = true;
        int currentPage = 1;
        int pageSize = 2;
        //        while (continueLoop) {
        //            RuleDataResult ruleDataResult = ruleGroupService.queryRuleData(ruleEngineContext, entityId, ruleCode, pageSize, currentPage);
        //            if (ruleDataResult == null || CollectionUtils.isEmpty(ruleDataResult.getData()) || ruleDataResult.getTotalNumber() == 0
        //                    || ruleDataResult.getTotalPage() == 0 || ruleDataResult.getTotalPage() > currentPage) {
        //                continueLoop = false;
        //            } else {
        //                this.datasAddVersion(context, entityId, newVersion, this.metadataDataGetIds(ruleDataResult.getData()));
        //            }
        //            currentPage++;
        //        }

        //新版本号更新为当前版本号/直接将新版本号更新为当前版本
        entityFieldShareCacheVersionService.updateRuleCurrentVersion(context, ruleCode, newVersion);
    }

    /**
     * 消费数据更新
     */
    @Override
    public void consumerAppDataUpdate(
            CommonContext context, String entityId, String dataId, String operator, Map<String, Object> dataMap) throws AuthServiceException {
    /*
     *
     * 1. 查询对象下的所有规则的规则版本expression
     *    1) redis缓存Map<RuleCode,Map<Version,Expression>>
     * 2. 调用数据规则引擎计算expression
     * 3. 查询数据目前匹配的规则,有差异直接更新(HashSet)
     *
     */
        if ("d".equals(operator) || MapUtils.isEmpty(dataMap)) {
            this.deleteDatas(context, entityId, Sets.newHashSet(dataId));
            return;
        }
        Set<String> currentMatchVersion = this.dataUpdateExpressionCalculatePatternRuleVersion(context, entityId, dataMap);
        if (CollectionUtils.isEmpty(currentMatchVersion)) {
            this.deleteDatas(context, entityId, Sets.newHashSet(dataId));
            return;
        }

        //ruleCode 下所有的version
        List<String> currentVersions = this.dataVersions(context, entityId, dataId);
        this.updateDataVersions(context, entityId, dataId, currentVersions);

    }

    private Set<String> dataUpdateExpressionCalculatePatternRuleVersion(
            CommonContext context, String entityId, Map<String, Object> dataMap) {
        //        Set<String> ruleCodes = ruleGroupService.entityRuleExpressionPattern(context, entityId, dataMap);

        return Sets.newHashSet();
    }

    /**
     * 重新计算数据的规则缓存
     */
    public void calculateDatasCache(CommonContext context, String entityId, Set<String> dataIds) {

    }

    /**
     * 删除数据
     */
    public void deleteDatas(CommonContext context, String entityId, Set<String> dataIds) {
        if (StringUtils.isNotBlank(entityId) && CollectionUtils.isNotEmpty(dataIds)) {
            entityFieldShareCacheMapper.deleteDatas(context.getTenantId(), context.getAppId(), entityId, dataIds);
        }
    }

    private List<String> metadataDataGetIds(List<Map<String, Object>> dataMapList) {
        List<String> ids = new LinkedList<>();
        if (CollectionUtils.isNotEmpty(dataMapList)) {
            dataMapList.forEach(dataMap -> {
                if (dataMap != null) {
                    ids.add((String) dataMap.get("_id"));
                }
            });
        }
        return ids;
    }

    private void tenantRuleSerial(CommonContext context, Set<String> ruleCodes) {
    /*
     * 每个企业保证只有一个ruleCode在重新计算缓存,避免死锁Map<tenantId,List<RuleCode>>
     *
     */

    }

}
