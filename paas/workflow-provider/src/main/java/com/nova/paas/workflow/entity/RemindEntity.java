package com.nova.paas.workflow.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * zhenghaibo
 * 2018/5/16 10:56
 */
@Data
public class RemindEntity implements Serializable {
    private static final long serialVersionUID = 2285827723248732094L;

    // 提醒类型：超时提醒：1，task 结束后：2
    private Integer remindType;
    // 提醒时间,task实例与超时时间相对的时间,具体的小时说如：-3（超时前3小时），3（超时后3小时）
    private Long remindLatency;
    // key(person,dept,group...)
    private Map<String, List<String>> remindTargets;
    // 提醒内容的content
    private String content;
    // 提醒内容的title
    private String title;
}
