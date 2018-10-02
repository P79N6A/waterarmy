package com.xiaopeng.waterarmy.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * * 功能描述：汽车网站平台枚举
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="1206401391@qq.com">iason</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/20
 */
public enum TaskTypeEnum {

    /**
     * 发帖
     */
    POSIED ("POSIED", "发帖"),

    /**
     * 评论
     */
    COMMENT ("COMMENT", "评论"),

    /**
     * 阅读
     */
    READ ("READ", "阅读"),

    /**
     * 评论点赞
     */
    LIKE ("LIKE", "评论点赞");

    private String name;
    private String desc;

    private TaskTypeEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public static List<Map<String, Object>> parseMap() {
        List<Map<String, Object>> lists = new ArrayList<>();
        for (TaskTypeEnum taskTypeEnum : TaskTypeEnum.values()) {
            Map<String, Object> taskType = new HashMap<>();
            taskType.put("name", taskTypeEnum.getName());
            taskType.put("desc", taskTypeEnum.getDesc());
            lists.add(taskType);
        }
        return lists;
    }

    public static String getDesc(String name) {
        for (TaskTypeEnum r : TaskTypeEnum.values()) {
            if (r.getName().equals(name)) {
                return r.desc;
            }
        }
        return null;
    }

    public static TaskTypeEnum getEnum(String name) {
        for (TaskTypeEnum r : TaskTypeEnum.values()) {
            if (r.getName().equals(name)) {
                return r;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
