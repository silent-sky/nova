package com.nova.paas.auth.pojo.permission;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QueryRecordPermissObjectFilter implements Serializable {
    private static final long serialVersionUID = 5482500761114753503L;
    private Integer memberType;
    private List<String> members;
    private String roleType;
    private Integer permission;
}
