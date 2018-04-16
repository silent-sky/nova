package com.nova.paas.auth.mapper.permission;

import com.nova.paas.auth.entity.permission.UserDeptRelationCache;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserDeptRelationCacheMapper {

    //  @DeleteProvider(type = UserDeptRelationCacheProvider.class, method = "delUserDeptRelationCache")
    void delUserDeptRelationCache(
            @Param("tenantId") String tenantId,
            @Param("users") Set<String> users,
            @Param("depts") Set<String> depts,
            @Param("userDeptRelationFlag") Integer userDeptRelationFlag);

    //  @DeleteProvider(type = UserDeptRelationCacheProvider.class, method = "delUserDeptMapCache")
    void delUserDeptMapCache(
            @Param("tenantId") String tenantId,
            @Param("map") Map<String, Set<String>> map,
            @Param("userDeptRelationFlag") Integer userDeptRelationFlag);

    //  @SelectProvider(type = UserDeptRelationCacheProvider.class, method = "userDeptsRelationCache")
    List<UserDeptRelationCache> userDeptsRelationCache(
            @Param("tenantId") String tenantId,
            @Param("users") Set<String> users,
            @Param("depts") Set<String> depts,
            @Param("userDeptRelationFlag") Integer userDeptRelationFlag);

    //  @SelectProvider(type = UserDeptRelationCacheProvider.class, method = "queryUserIdByLeaderId")
    Set<String> queryUserIdByLeaderId(@Param("tenantId") String tenantId, @Param("leaderId") String leaderId);

    //  @SelectProvider(type = UserDeptRelationCacheProvider.class, method = "deptLeaderSubordinates")
    Set<String> deptLeaderSubordinates(
            @Param("tenantId") String tenantId,
            @Param("users") Collection<String> users,
            @Param("leaderId") String leaderId,
            @Param("userDeptRelationFlag") Integer userDeptRelationFlag);

    //  @SelectProvider(type = UserDeptRelationCacheProvider.class, method = "userSubUsersAndLeaderDeptUsers")
    Set<String> userSubUsersAndLeaderDeptUsers(
            @Param("tenantId") String tenantId,
            @Param("users") Collection<String> users,
            @Param("leaderId") String leaderId,
            @Param("userDeptRelationFlag") Integer userDeptRelationFlag,
            @Param("userLeaderRelationFlag") Integer userLeaderRelationFlag);

}
