package com.nova.paas.auth.service.support;

import com.nova.paas.auth.service.entity.FieldAccess;
import com.nova.paas.auth.service.entity.FuncAccess;
import com.nova.paas.auth.service.entity.Function;
import com.nova.paas.auth.service.entity.RecordTypeAccess;
import com.nova.paas.auth.service.entity.Role;
import com.nova.paas.auth.service.entity.UserRole;
import com.nova.paas.auth.service.entity.ViewAccess;
import com.nova.paas.auth.service.mapper.FieldAccessMapper;
import com.nova.paas.auth.service.mapper.FuncAccessMapper;
import com.nova.paas.auth.service.mapper.FunctionMapper;
import com.nova.paas.auth.service.mapper.RecordTypeAccessMapper;
import com.nova.paas.auth.service.mapper.RoleMapper;
import com.nova.paas.auth.service.mapper.UserRoleMapper;
import com.nova.paas.auth.service.mapper.ViewAccessMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * zhenghaibo
 * 18/4/11 15:23
 */
@Service("tenantServiceUtilService")
@Slf4j
public class TenantServiceUtilService {

    @Autowired
    FunctionMapper funcMapper;
    @Autowired
    FuncAccessMapper funcAccessMapper;
    @Autowired
    FieldAccessMapper fieldAccessMapper;
    @Autowired
    ViewAccessMapper viewAccessMapper;
    @Autowired
    UserRoleMapper userRoleMapper;
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    RecordTypeAccessMapper recordTypeAccessMapper;

    @Transactional
    public void enterpriseCopyBatchInsert(
            String shardTenantId,
            List<Function> functionList,
            List<Role> roleList,
            List<UserRole> userRoleList,
            List<FuncAccess> funcAccessList,
            List<FieldAccess> fieldAccessList,
            List<ViewAccess> viewAccessList,
            List<RecordTypeAccess> recordTypeAccessList) {
        if (CollectionUtils.isNotEmpty(functionList)) {
            //            funcMapper.batchInsert(functionList);
        }
        if (CollectionUtils.isNotEmpty(roleList)) {
            //            roleMapper.batchInsert(roleList);
        }
        if (CollectionUtils.isNotEmpty(userRoleList)) {
            //            userRoleMapper.batchInsert(userRoleList);
        }
        if (CollectionUtils.isNotEmpty(funcAccessList)) {
            //            funcAccessMapper.batchInsert(funcAccessList);
        }
        if (CollectionUtils.isNotEmpty(fieldAccessList)) {
            //            fieldAccessMapper.batchInsert(fieldAccessList);
        }
        if (CollectionUtils.isNotEmpty(viewAccessList)) {
            //            viewAccessMapper.batchInsert(viewAccessList);
        }
        if (CollectionUtils.isNotEmpty(recordTypeAccessList)) {
            //            recordTypeAccessMapper.batchInsert(recordTypeAccessList);
        }
    }
}
