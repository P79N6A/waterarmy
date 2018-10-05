package com.xiaopeng.waterarmy.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 内容库枚举类
 *
 * Created by iason on 2018/10/5.
 */
public enum ContentRepositoriesEnum {


    /**
     * 发帖
     */
    POSIED ("POSIED", "发帖"),

    /**
     * 评论
     */
    COMMENT ("COMMENT", "评论");

    private String name;
    private String desc;

    private ContentRepositoriesEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public static List<Map<String, Object>> parseMap() {
        List<Map<String, Object>> lists = new ArrayList<>();
        for (ContentRepositoriesEnum contentRepositoriesEnum : ContentRepositoriesEnum.values()) {
            Map<String, Object> taskType = new HashMap<>();
            taskType.put("name", contentRepositoriesEnum.getName());
            taskType.put("desc", contentRepositoriesEnum.getDesc());
            lists.add(taskType);
        }
        return lists;
    }

    public static String getDesc(String name) {
        for (ContentRepositoriesEnum r : ContentRepositoriesEnum.values()) {
            if (r.getName().equals(name)) {
                return r.desc;
            }
        }
        return null;
    }

    public static ContentRepositoriesEnum getEnum(String name) {
        for (ContentRepositoriesEnum r : ContentRepositoriesEnum.values()) {
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
