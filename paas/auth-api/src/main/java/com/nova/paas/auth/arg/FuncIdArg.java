package com.nova.paas.auth.arg;

import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/4/21 21:38
 */
@Data
public class FuncIdArg implements Serializable {
    private static final long serialVersionUID = -3913799477192618297L;

    private String id;
}
