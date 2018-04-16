package com.nova.paas.auth.pojo.permission;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class EntityObjects implements Serializable {

    private static final long serialVersionUID = -8420032692461083494L;
    private String entityId;
    private Set<String> objects;
}
