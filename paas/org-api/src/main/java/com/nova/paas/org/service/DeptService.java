package com.nova.paas.org.service;

import com.nova.paas.common.pojo.CommonContext;
import com.nova.paas.org.pojo.DeptPojo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/16 19:17
 */
public interface DeptService {
    Map<String, List<DeptPojo>> batchQuerySuperDeptPathWithSelfByDeptId(CommonContext context, Set<String> deptIds);
}
