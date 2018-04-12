package com.nova.paas.auth.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/4/8 19:30
 */
@Data
public class FieldAccessPojo implements Serializable {
    private static final long serialVersionUID = 602040845692413762L;

    private String fieldId;
    private Integer permission;
}
