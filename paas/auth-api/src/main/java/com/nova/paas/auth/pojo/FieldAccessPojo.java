package com.nova.paas.auth.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/4/8 19:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldAccessPojo implements Serializable {
    private static final long serialVersionUID = 602040845692413762L;

    private String tenantId;
    private String fieldId;
    private Integer permission;
}
