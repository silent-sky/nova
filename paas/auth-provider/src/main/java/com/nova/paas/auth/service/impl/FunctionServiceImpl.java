package com.nova.paas.auth.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nova.paas.auth.entity.Function;
import com.nova.paas.auth.exception.AuthErrorMsg;
import com.nova.paas.auth.exception.AuthException;
import com.nova.paas.auth.exception.AuthServiceException;
import com.nova.paas.auth.mapper.FunctionMapper;
import com.nova.paas.auth.param.QryFunctionParam;
import com.nova.paas.auth.pojo.FunctionPojo;
import com.nova.paas.auth.service.FunctionService;
import com.nova.paas.common.constant.AuthConstant;
import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.common.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * zhenghaibo
 * 18/4/11 15:23
 */
@Service
@Slf4j
public class FunctionServiceImpl implements FunctionService {
    @Inject
    FunctionMapper functionMapper;

    @Override
    public List<FunctionPojo> queryFunction(CommonContext context, QryFunctionParam param) throws AuthServiceException {
        List<Function> funcList =
                functionMapper.queryFunction(context.getTenantId(), param.getIds(), param.getFuncName(), param.getParentId(), param.getFuncType());
        return this.convertFuncToPojos(funcList);
    }

    @Override
    @Transactional
    public void addFunc(CommonContext context, FunctionPojo pojo) throws AuthServiceException {
        Function entity = new Function();
        try {
            PropertyUtils.copyProperties(entity, pojo);
            entity.setId(IdUtil.generateId());
        } catch (Exception e) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        functionMapper.insert(entity);
    }

    @Override
    @Transactional
    public void batchDeleteFunc(CommonContext context, Set<String> funcIdSet) throws AuthServiceException {
        functionMapper.batchDelete(context.getTenantId(), context.getUserId(), funcIdSet, System.currentTimeMillis());
    }

    @Override
    @Transactional
    public void updateFunc(CommonContext context, FunctionPojo functionPojo) throws AuthServiceException {
        this.functionPojoVerify(functionPojo);  //参数校验

        if (StringUtils.isBlank(functionPojo.getId())) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
        //校验function是否存在
        List<Function> functionList = functionMapper.queryFunction(context.getTenantId(), Sets.newHashSet(functionPojo.getId()), null, null, null);

        if (CollectionUtils.isEmpty(functionList)) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        Function function = functionList.get(0);
        if (function.getFuncType() != null && AuthConstant.FuncType.DEFAULT == function.getFuncType()) {
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        //记录父亲节点
        String oldParentId = function.getParentId();
        function.setFuncName(functionPojo.getFuncName());
        function.setFuncOrder(functionPojo.getFuncOrder());
        function.setParentId(functionPojo.getParentId());
        function.setModifiedBy(context.getUserId());
        function.setModifiedAt(System.currentTimeMillis());

        //parentCode出现变更,检测回路
        if (!function.getParentId().equals(oldParentId)) {
            Set<String> tempSet = this.queryAllFuncIds(context.getTenantId());
            if (CollectionUtils.isEmpty(tempSet)) {
                throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
            //parentId是否存在
            if (!tempSet.contains(function.getParentId())) {
                throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }

            List<FunctionPojo> functionPojoList = this.queryAllFunctions(context.getTenantId());

            //funcPojoList 更新父节点
            for (FunctionPojo funcPojo : functionPojoList) {
                if (funcPojo.getFuncCode().equals(functionPojo.getFuncCode())) {
                    funcPojo.setParentId(function.getParentId());
                    break;
                }
            }
            tempSet.add(AuthConstant.TreeRoot.ROOT);
            //存储funcId对应的funcName
            Map<String, String> idNameMap = new HashMap<>();
            //存储树形结构
            Map<String, List<String>> funcTree = new HashMap<>();
            //功能树构建
            this.funcTreeBuild(functionPojoList, funcTree, idNameMap);

            //检测回路
            Set<String> funcSet = this.loopCheck(funcTree, idNameMap.keySet(), AuthConstant.TreeRoot.ROOT);
            if (CollectionUtils.isNotEmpty(funcSet)) {
                throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
        }
        try {
            //更新数据库
            functionMapper.update(function);

            //删除功能function缓存
            //            cacheManager.delKey(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION);
        } catch (Exception e) {
            log.error("===auth.updateFunc() error===", e);
            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
        }

    }

    private List<FunctionPojo> convertFuncToPojos(List<Function> functionList) {
        List<FunctionPojo> pojoList = Lists.newArrayList();
        for (Function func : functionList) {
            FunctionPojo pojo = new FunctionPojo();
            try {
                PropertyUtils.copyProperties(pojo, func);
            } catch (Exception e) {
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
            pojoList.add(pojo);
        }
        return pojoList;
    }

    /************************************** separate ***********************************************/

    //    @Override
    //    @SuppressWarnings("unchecked")
    //    public List<FunctionPojo> queryFunctionByTenant(CommonContext context) throws AuthServiceException {
    //        List<FunctionPojo> functionPojoList = null;
    //        try {
    //            functionPojoList = (List<FunctionPojo>) cacheManager.getHashObject(
    //                    context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION,
    //                    context.getAppId());
    //        } catch (Exception e) {
    //            functionPojoList = this.queryFunctionByTenantFromDB(context);
    //            return functionPojoList;
    //        }
    //
    //        if (functionPojoList == null) {
    //            //查询数据库
    //            functionPojoList = this.queryFunctionByTenantFromDB(context);
    //            try {
    //                cacheManager.putHashObject(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION,
    //                        context.getAppId(),
    //                        functionPojoList);
    //                //                cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION, FUNCTION_EXPIRE_SECOND);
    //            } catch (Exception e) {
    //                return functionPojoList;
    //            }
    //        }
    //        return functionPojoList;
    //    }
    //
    //    private void batchAddFunc(CommonContext context, List<FunctionPojo> functionPojoList) throws AuthServiceException {
    //        log.info("[Request], method:{},context:{},functionPojoList:{}", "addFunc", JSON.toJSONString(context), JSON.toJSONString(functionPojoList));
    //
    //        if (CollectionUtils.isEmpty(functionPojoList)) {
    //            return;
    //        }
    //        //已存在的功能
    //        List<FunctionPojo> dbFuncPojoList = this.querySimpleFunctionFromDB(context);
    //
    //        //参数校验
    //        this.funcPojoVerify(context, dbFuncPojoList, functionPojoList);
    //
    //        //添加新功能
    //        List<Function> functionList = new ArrayList<>();
    //        try {
    //            for (FunctionPojo functionPojo : functionPojoList) {
    //                Function function = new Function();
    //
    //                function.setId(IdUtil.generateId());
    //                function.setAppId(context.getAppId());
    //                function.setTenantId(context.getTenantId());
    //
    //                function.setFuncCode(functionPojo.getFuncCode());
    //                function.setParentId(functionPojo.getParentId());
    //                function.setFuncOrder(functionPojo.getFuncOrder());
    //                function.setFuncName(functionPojo.getFuncName());
    //                function.setFuncType(functionPojo.getFuncType());
    //
    //                function.setCreatedBy(context.getUserId());
    //                function.setCreatedAt(System.currentTimeMillis());
    //                function.setDelFlag(Boolean.FALSE);
    //
    //                //functionPojo缺少id等属性值
    //                PropertyUtils.copyProperties(functionPojo, function);
    //
    //                functionList.add(function);
    //            }
    //        } catch (Exception e) {
    //            log.error("addFunc  function  convert to funcPojo error{}", JSON.toJSONString(functionPojoList), e);
    //            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
    //        }
    //        dbFuncPojoList.addAll(functionPojoList);
    //
    //        //回路检测
    //        this.loopCheck(dbFuncPojoList);
    //        try {
    //            //            functionMapper.batchInsert(functionList);
    //
    //            //            cacheManager.delKey(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION);
    //        } catch (Exception e) {
    //            log.error("===auth.addFunc() error===", e);
    //            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
    //        }
    //    }
    //
    //    @Override
    //    @Transactional
    //    public void updateFunc(CommonContext context, FunctionPojo functionPojo) throws AuthServiceException {
    //
    //        log.info("[Request],method:{}, context:{},functionPojo:{}", "updateFunc", JSON.toJSONString(context), JSON.toJSONString(functionPojo));
    //
    //        this.functionPojoVerify(functionPojo);  //参数校验
    //
    //        if (StringUtils.isBlank(functionPojo.getFuncCode())) {
    //            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
    //        }
    //        List<Function> functionList;
    //        try {
    //            //校验function是否存在
    //            functionList = functionMapper.queryFunction(context.getTenantId(),
    //                    context.getAppId(),
    //                    null,
    //                    Collections.singleton(functionPojo.getFuncCode()),
    //                    null,
    //                    null,
    //                    null,
    //                    null,
    //                    null,
    //                    false);
    //        } catch (Exception e) {
    //            log.error("===auth.updateFunc() error===", e);
    //            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
    //        }
    //        if (CollectionUtils.isEmpty(functionList)) {
    //            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
    //        }
    //        Function function = functionList.get(0);
    //        if (function.getFuncType() != null && AuthConstant.FuncType.DEFAULT == function.getFuncType()) {
    //            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
    //        }
    //        String oldParentId = function.getParentId(); //记录父亲节点
    //        function.setFuncName(functionPojo.getFuncName());
    //        function.setFuncOrder(functionPojo.getFuncOrder());
    //        function.setParentId(functionPojo.getParentId());
    //        function.setModifiedBy(context.getUserId());
    //        function.setModifiedAt(System.currentTimeMillis());
    //
    //        //parentCode出现变更,检测回路
    //        if (!function.getParentId().equals(oldParentId)) {
    //            Set<String> tempSet = new HashSet<>();
    //            try {
    //                tempSet = functionMapper.queryFunctionCode(context.getTenantId(),
    //                        context.getAppId(),
    //                        null,
    //                        null,
    //                        null,
    //                        null,
    //                        null,
    //                        null,
    //                        null,
    //                        Boolean.FALSE);
    //            } catch (Exception e) {
    //                log.error("===auth.updateFunc() error===", e);
    //                throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
    //            }
    //            if (CollectionUtils.isEmpty(tempSet)) {
    //                throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
    //            }
    //            //parentCode是否存在
    //            if (!tempSet.contains(function.getParentId())) {
    //                throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
    //            }
    //
    //            List<FunctionPojo> functionPojoList = this.queryFunctionByTenant(context);
    //
    //            //funcPojoList 更新父节点
    //            for (FunctionPojo funcPojo : functionPojoList) {
    //                if (funcPojo.getFuncCode().equals(functionPojo.getFuncCode())) {
    //                    funcPojo.setParentId(function.getParentId());
    //                    break;
    //                }
    //            }
    //            tempSet.add(AuthConstant.TreeRoot.ROOT);
    //            Map<String, String> funcCodeFuncNameMap = new HashMap<>(); //存储funcCode对应的funcName
    //            Map<String, List<String>> funcTree = new HashMap<>(); //存储树形结构
    //            this.funcTreeBuild(functionPojoList, funcTree, funcCodeFuncNameMap);  //功能树构建
    //
    //            //检测回路
    //            Set<String> funcSet = this.loopCheck(funcTree, funcCodeFuncNameMap.keySet(), AuthConstant.TreeRoot.ROOT); //功能是否出现回路检测
    //            if (CollectionUtils.isNotEmpty(funcSet)) {
    //                throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
    //            }
    //        }
    //        try {
    //            //更新数据库
    //            //            functionMapper.update(function);
    //
    //            //删除功能function缓存
    //            //            cacheManager.delKey(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION);
    //        } catch (Exception e) {
    //            log.error("===auth.updateFunc() error===", e);
    //            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
    //        }
    //    }
    //
    //    @Override
    //    @Transactional
    //    public void delFunc(CommonContext context, String funcId) throws AuthServiceException {
    //        log.info("[Request], method:{},context:{},funcId:{}", "delFunc", JSON.toJSONString(context), JSON.toJSONString(funcId));
    //
    //        if (StringUtils.isBlank(funcId)) {
    //            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
    //        }
    //        /*
    //        * 删除所有的子功能(数据库,缓存)
    //        * 1.查询所有待删除的funcCode
    //        * 2.删除func表的所有funcCode,并更新缓存数据
    //        * */
    //        List<FunctionPojo> functionPojoList = this.queryFunctionByTenant(context);
    //        FunctionPojo tempFunctionPojo = null;
    //        for (FunctionPojo functionPojo : functionPojoList) {
    //            if (funcId.equals(functionPojo.getId())) {
    //                tempFunctionPojo = functionPojo;
    //                break;
    //            }
    //        }
    //        if (tempFunctionPojo == null) {
    //            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
    //        }
    //
    //        //获取子功能
    //        List<String> childrenFuncCode = this.queryFuncChildren(functionPojoList, tempFunctionPojo.getFuncCode());
    //        Iterator<FunctionPojo> iter = functionPojoList.iterator();
    //        while (iter.hasNext()) {
    //            tempFunctionPojo = iter.next();
    //            if (childrenFuncCode.contains(tempFunctionPojo.getFuncCode())) {
    //                iter.remove();
    //            }
    //        }
    //        /*
    //         *更新所有的角色权限(数据库,缓存)
    //         * 1.更新funcAccess所有角色权限
    //         * 2.从缓存中获取角色的功能权限,更新所有角色的功能权限
    //         */
    //        try {
    //            //功能
    //            functionMapper.batchDel(context.getTenantId(), context.getAppId(), context.getUserId(), childrenFuncCode, System.currentTimeMillis());
    //
    //            //功能权限
    //            functionAccessMapper.batchDel(context.getTenantId(),
    //                    context.getAppId(),
    //                    null,
    //                    context.getUserId(),
    //                    childrenFuncCode,
    //                    System.currentTimeMillis());
    //
    //            cacheManager.putHashObject(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION,
    //                    context.getAppId(),
    //                    functionPojoList);
    //            //            cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION, FUNCTION_EXPIRE_SECOND);
    //
    //            Map<String, Set<String>> permissionMap = null;
    //
    //            permissionMap = (Map<String, Set<String>>) cacheManager.getHashEntries(
    //                    context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION_PERMISSION);
    //            if (permissionMap == null || permissionMap.isEmpty()) {
    //                return;
    //            }
    //            permissionMap.forEach((role, funcPermiss) -> {
    //                if (CollectionUtils.isNotEmpty(funcPermiss)) {
    //                    funcPermiss.removeAll(childrenFuncCode);
    //                }
    //            });
    //
    //            cacheManager.putAll(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION_PERMISSION,
    //                    (Map) permissionMap);
    //            //            cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION, FUNCTION_EXPIRE_SECOND);
    //
    //        } catch (Exception e) {
    //            log.error("===auth.delFunc() error===", e);
    //            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
    //        }
    //    }
    //
    //    /**
    //     * 查询业务功能
    //     */
    //    public List<FunctionPojo> queryFunction(
    //            CommonContext context,
    //            Set<String> ids,
    //            Set<String> funcCodes,
    //            String funcName,
    //            String funcOrder,
    //            String levelCode,
    //            String parentCode,
    //            Integer funcType) throws AuthServiceException {
    //        List<FunctionPojo> functionPojoList = new LinkedList<>();
    //        if (ids != null) {
    //            ids.remove(null);
    //        }
    //        if (funcCodes != null) {
    //            funcCodes.remove(null);
    //        }
    //        if (ids != null && ids.isEmpty()) {
    //            return functionPojoList;
    //        }
    //        if (funcCodes != null && funcCodes.isEmpty()) {
    //            return functionPojoList;
    //        }
    //        try {
    //            List<Function> functionList = functionMapper.queryFunction(context.getTenantId(),
    //                    context.getAppId(),
    //                    ids,
    //                    funcCodes,
    //                    funcName,
    //                    funcOrder,
    //                    levelCode,
    //                    parentCode,
    //                    funcType,
    //                    Boolean.FALSE);
    //            if (CollectionUtils.isNotEmpty(functionList)) {
    //                for (Function function : functionList) {
    //                    FunctionPojo functionPojo = new FunctionPojo();
    //                    PropertyUtils.copyProperties(functionPojo, function);
    //                    functionPojoList.add(functionPojo);
    //                }
    //            }
    //        } catch (Exception e) {
    //            log.error("===auth.queryFunction() error===", e);
    //            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
    //        }
    //        return functionPojoList;
    //    }
    //
    //    @Transactional
    //    @Override
    //    public void batchDelFunc(CommonContext context, Set<String> funcSet) throws AuthServiceException {
    //        log.info("[Request], method:{},context:{},funcSet:{}", "batchDelFunc", JSON.toJSONString(context), JSON.toJSONString(funcSet));
    //
    //        if (funcSet != null) {
    //            funcSet.remove(null);
    //        }
    //        if (CollectionUtils.isEmpty(funcSet)) {
    //            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
    //        }
    //        for (String funcCode : funcSet) {
    //            if (StringUtils.isBlank(funcCode)) {
    //                throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
    //            }
    //        }
    //        /*
    //        * 删除所有的子功能(数据库,缓存)
    //        * 1.查询所有待删除的funcCode
    //        * 2.删除func表的所有funcCode,并更新缓存数据
    //        * */
    //        List<FunctionPojo> functionPojoList = this.queryFunctionByTenant(context);
    //        //        Set<String> dbFunc = new HashSet<>();
    //        //        functionPojoList.forEach(pojo -> {
    //        //            dbFunc.add(pojo.getFuncCode());
    //        //        });
    //
    //        //获取子功能
    //        Set<String> allFunc = new HashSet<>();
    //        allFunc.addAll(funcSet);
    //        for (String funcCode : funcSet) {
    //            allFunc.addAll(this.queryFuncChildren(functionPojoList, funcCode));
    //        }
    //
    //        //清理functionPojoList
    //        Iterator<FunctionPojo> iter = functionPojoList.iterator();
    //        FunctionPojo tempFunctionPojo;
    //        while (iter.hasNext()) {
    //            tempFunctionPojo = iter.next();
    //            if (allFunc.contains(tempFunctionPojo.getFuncCode())) {
    //                iter.remove();
    //            }
    //        }
    //        /*
    //         *更新所有的角色权限(数据库,缓存)
    //         * 1.更新funcAccess所有角色权限
    //         * 2.从缓存中获取角色的功能权限,更新所有角色的功能权限
    //         */
    //        try {
    //            //功能
    //            functionMapper.batchDel(context.getTenantId(),
    //                    context.getAppId(),
    //                    context.getUserId(),
    //                    new ArrayList<>(allFunc),
    //                    System.currentTimeMillis());
    //
    //            //功能权限
    //            functionAccessMapper.batchDel(context.getTenantId(), context.getAppId(), null, context.getUserId(), allFunc, System.currentTimeMillis());
    //
    //            cacheManager.putHashObject(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION,
    //                    context.getAppId(),
    //                    functionPojoList);
    //            //            cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION, FUNCTION_EXPIRE_SECOND);
    //
    //            Map<String, Set<String>> permissionMap = null;
    //
    //            permissionMap = (Map<String, Set<String>>) cacheManager.getHashEntries(
    //                    context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION_PERMISSION);
    //            if (permissionMap == null || permissionMap.isEmpty()) {
    //                return;
    //            }
    //            final Set<String> finalAllFunc = allFunc;
    //            permissionMap.forEach((role, funcPermiss) -> {
    //                if (CollectionUtils.isNotEmpty(funcPermiss)) {
    //                    funcPermiss.removeAll(finalAllFunc);
    //                }
    //            });
    //
    //            cacheManager.putAll(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION_PERMISSION,
    //                    (Map) permissionMap);
    //            //            cacheManager.expire(context.getTenantId() + ":" + context.getAppId() + ":" + AuthConstant.AuthType.AUTH_FUNCTION_PERMISSION, FUNCTION_EXPIRE_SECOND);
    //
    //        } catch (Exception e) {
    //            log.error("===auth.batchDelFunc() error===", e);
    //            throw new AuthServiceException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
    //        }
    //    }
    //

    /**
     * 回路检测
     *
     * @param funcTree 邻接表树形存储
     * @param treeNode 节点列表
     * @param treeRoot 根节点列表
     * @return 回路节点
     */
    private Set<String> loopCheck(Map<String, List<String>> funcTree, Set<String> treeNode, String treeRoot) {
        Queue<String> nodeQueue = new LinkedList<>();
        nodeQueue.add(treeRoot);
        String tempNode;
        while (!nodeQueue.isEmpty()) {
            tempNode = nodeQueue.poll();
            if (CollectionUtils.isNotEmpty(funcTree.get(tempNode))) {
                nodeQueue.addAll(funcTree.get(tempNode));
            }
            treeNode.remove(tempNode);
        }
        return treeNode;
    }

    /**
     * 回路检测
     *
     * @param functionPojoList 功能列表
     */
    private Set<String> loopCheck(List<FunctionPojo> functionPojoList) {
        Map<String, String> funcCodeFuncNameMap = new HashMap<>(); //存储funcCode对应的funcName
        Map<String, List<String>> funcTree = new HashMap<>(); //存储树形结构
        structureFuncTree(functionPojoList, funcCodeFuncNameMap, funcTree);

        //回路检测
        return this.loopCheck(funcTree, funcCodeFuncNameMap.keySet(), AuthConstant.TreeRoot.ROOT);
    }

    /**
     * 构建功能树,功能唯一标识对应的功能名
     *
     * @param functionPojoList 功能树列表
     * @param funcTree         功能树
     * @param funcCodeNameMap  功能唯一标识对应的功能名
     */
    private void funcTreeBuild(List<FunctionPojo> functionPojoList, Map<String, List<String>> funcTree, Map<String, String> funcCodeNameMap) {
        structureFuncTree(functionPojoList, funcCodeNameMap, funcTree);
    }

    /**
     * 查询功能节点的子功能(包括自身)
     */
    private List<String> queryFuncChildren(List<FunctionPojo> functionPojoList, String funcCode) {
        List<String> node = new LinkedList<>();
        Map<String, List<String>> funcTree = new HashMap<>(); //存储树形结构
        for (FunctionPojo functionPojo : functionPojoList) {
            if (functionPojo.getParentId().equals(AuthConstant.TreeRoot.ROOT)) {
                continue;
            }
            addFuncPojoToFuncTree(funcTree, functionPojo);
        }
        Queue<String> nodeQueue = new LinkedList<>();
        nodeQueue.add(funcCode);
        String tempNode = null;
        while (!nodeQueue.isEmpty()) {
            tempNode = nodeQueue.poll();
            if (CollectionUtils.isNotEmpty(funcTree.get(tempNode))) {
                nodeQueue.addAll(funcTree.get(tempNode));
            }
            node.add(tempNode);
        }
        return node;
    }

    private void functionPojoVerify(FunctionPojo functionPojo) {
        if (functionPojo == null) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        //功能名
        if (StringUtils.isBlank(functionPojo.getFuncName())) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        //功能父节点
        if (StringUtils.isBlank(functionPojo.getParentId())) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }

        //节点不能自己指向自己(回路)
        if (functionPojo.getId() != null && functionPojo.getId().equals(functionPojo.getParentId())) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    /**
     * 功能唯一标识校验
     */
    private void funcCodeVerify(String id) {
        //功能唯一标识
        if (StringUtils.isBlank(id)) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    /**
     * 功能节点数据校验
     */
    private void funcPojoVerify(CommonContext context, List<FunctionPojo> dbFuncPojoList, List<FunctionPojo> funcPojoList) {
        Set<String> tempSet = new HashSet<>();
        dbFuncPojoList.forEach(functionPojo -> {
            tempSet.add(functionPojo.getFuncCode());
        });

        //function字段检测,唯一标识重复检测
        funcPojoList.forEach(functionPojo -> {
            this.functionPojoVerify(functionPojo);  //字段检测
            if (functionPojo.getFuncType() == null) {
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
            if (tempSet.contains(functionPojo.getFuncCode())) {
                throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
            }
            tempSet.add(functionPojo.getFuncCode());
        });

        //parentId检测
        funcPojoList.forEach(functionPojo -> {
            if (!tempSet.contains(functionPojo.getParentId())) {
                if (!AuthConstant.TreeRoot.ROOT.equals(functionPojo.getParentId())) {
                    throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
                }
            }
        });
    }

    private void isLoop(List<FunctionPojo> funcPojoList) {
        Set<String> loopNode = this.loopCheck(funcPojoList);
        if (loopNode != null && loopNode.size() > 0) {
            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
        }
    }

    /**
     * 校验同一级的功能是否功能名重复
     *
     * @param funcTree     功能树结构
     * @param funcCodeName funcCode对应的funcName
     */
    private void funcNameSameLevelRepectCheck(Map<String, List<String>> funcTree, Map<String, String> funcCodeName) {
        if (funcTree == null || funcTree.isEmpty()) {
            return;
        }
        if (funcCodeName == null || funcCodeName.isEmpty()) {
            return;
        }
        Set<String> tempSet = new HashSet<>();
        funcTree.forEach((funcCode, children) -> {
            children.forEach(child -> {
                if (tempSet.contains(funcCodeName.get(child))) {
                    throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION);
                }
                tempSet.add(funcCodeName.get(child));
            });
            tempSet.clear();
        });
    }

    /**
     * 查询企业应用功能(mysql)
     */
    //    private List<FunctionPojo> queryFunctionByTenantFromDB(CommonContext context) {
    //        //shard.setShardId(authContext.getTenantId());
    //        List<FunctionPojo> functionPojoList = new LinkedList<>();
    //        try {
    //            List<Function> functionList = functionMapper.queryFunctionByTenant(context.getTenantId(), context.getAppId());
    //            if (CollectionUtils.isEmpty(functionList)) {
    //                return functionPojoList;
    //            }
    //            for (Function function : functionList) {
    //                FunctionPojo functionPojo = new FunctionPojo();
    //
    //                functionPojo.setId(function.getId());
    //                functionPojo.setTenantId(function.getTenantId());
    //                functionPojo.setFuncCode(function.getFuncCode());
    //                functionPojo.setParentId(function.getParentId());
    //                functionPojo.setFuncName(function.getFuncName());
    //                functionPojo.setFuncType(function.getFuncType());
    //                functionPojo.setFuncOrder(function.getFuncOrder());
    //
    //                functionPojoList.add(functionPojo);
    //            }
    //        } catch (Exception e) {
    //            log.error("===auth.queryFunctionByTenantFromDB() error===", e);
    //            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
    //        }
    //        return functionPojoList;
    //    }

    /**
     * 构建功能树结构
     */
    private void structureFuncTree(List<FunctionPojo> functionPojoList, Map<String, String> funcCodeFuncNameMap, Map<String, List<String>> funcTree) {
        for (FunctionPojo funcPojo : functionPojoList) {
            funcCodeFuncNameMap.put(funcPojo.getFuncCode(), funcPojo.getFuncName());
            addFuncPojoToFuncTree(funcTree, funcPojo);
        }
    }

    /**
     * 将每个功能节点添加到功能树结构中
     */
    private void addFuncPojoToFuncTree(Map<String, List<String>> funcTree, FunctionPojo funcPojo) {
        if (funcTree.containsKey(funcPojo.getParentId())) {
            //添加子节点
            funcTree.get(funcPojo.getParentId()).add(funcPojo.getId());
        } else {
            List<String> tempList = new LinkedList<>();
            tempList.add(funcPojo.getFuncCode());
            funcTree.put(funcPojo.getParentId(), tempList);
        }
    }

    //    private List<FunctionPojo> querySimpleFunctionFromDB(CommonContext context) {
    //        List<FunctionPojo> functionPojoList = new LinkedList<>();
    //        try {
    //            List<Function> functionList = functionMapper.querySimpleFunctionByTenant(context.getTenantId(), context.getAppId());
    //            if (CollectionUtils.isEmpty(functionList)) {
    //                return functionPojoList;
    //            }
    //            for (Function function : functionList) {
    //                FunctionPojo functionPojo = new FunctionPojo();
    //                functionPojo.setId(function.getId());
    //                functionPojo.setParentId(function.getParentId());
    //                functionPojoList.add(functionPojo);
    //            }
    //        } catch (Exception e) {
    //            log.error("===auth.addFuncPojoToFuncTree() error===", e);
    //            throw new AuthException(AuthErrorMsg.PAAS_AUTH_DEFAULT_EXCEPTION, e);
    //        }
    //        return functionPojoList;
    //    }

    private Set<String> queryAllFuncIds(String tenantId) {
        Set<String> idSet = Sets.newHashSet();
        List<Function> functionList = functionMapper.queryFunction(tenantId, null, null, null, null);
        if (CollectionUtils.isNotEmpty(functionList)) {
            for (Function func : functionList) {
                idSet.add(func.getId());
            }
        }
        return idSet;
    }

    private List<FunctionPojo> queryAllFunctions(String tenantId) {
        List<Function> functionList = functionMapper.queryFunction(tenantId, null, null, null, null);
        List<FunctionPojo> pojoList = convertFuncToPojos(functionList);

        return pojoList;
    }
}
