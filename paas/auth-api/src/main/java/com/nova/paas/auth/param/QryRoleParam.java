package com.nova.paas.auth.param;

import com.nova.paas.common.pojo.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * zhenghaibo
 * 2018/4/22 10:07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QryRoleParam implements Serializable {
    private static final long serialVersionUID = 1088273952788120373L;

    private Set<String> roleIds;
    private String roleName;
    private String roleCode;
    private Integer roleType;
    private PageInfo pageInfo;
}
