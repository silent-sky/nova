package com.nova.paas.common.util;

import org.bson.types.ObjectId;

/**
 * zhenghaibo
 * 2018/1/11 19:11
 */
public class IdUtil {
  public static String generateId() {
    return new ObjectId().toString();
  }
}
