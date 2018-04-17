package com.effektif.workflow.api.ext;

import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2017/4/8 13:24
 */
@Data
public class ActionParam implements Serializable {
    private String fromField;
    private String toField;
    private String fieldFlag;
    private Object fieldValue;
}
