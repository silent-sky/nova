package com.nova.paas.common.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * zhenghaibo
 * 2018/1/11 19:08
 */
@Data
public class CommonContext implements Serializable {
    private static final long serialVersionUID = -210272701971284679L;
    private String tenantId;
    private String appId;
    private String userId;
    private Map<String, Object> properties;

    public CommonContext tenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public CommonContext appId(String appId) {
        this.appId = appId;
        return this;
    }

    public CommonContext userId(String userId) {
        this.userId = userId;
        return this;
    }

    public CommonContext property(String key, Object value) {
        if (properties == null) {
            this.properties = new HashMap<>();
        }
        this.properties.put(key, value);
        return this;
    }
}
