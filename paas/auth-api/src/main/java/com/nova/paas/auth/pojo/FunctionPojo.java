package com.nova.paas.auth.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * zhenghaibo
 * 2018/1/12 10:23
 */
@Data
public class FunctionPojo implements Serializable {
  private static final long serialVersionUID = -5568682306872628657L;

  private String id;
  private String tenantId;
  private String appId;
  private String funcCode;
  private String funcName;
  private String funcOrder;
  private String parentCode;
  private String levelCode;
  private Integer funcType;
  private Boolean isEnabled;

}
