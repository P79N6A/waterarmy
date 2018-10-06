package com.xiaopeng.waterarmy.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * * 功能描述：任务状态枚举
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="1206401391@qq.com">iason</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/20
 */
public enum ExecuteStatusEnum {
    /**
     * 失败
     */
    FAIL (0, "失败"),

    /**
     * 成功
     */
    SUCCEED (1, "成功");

    private int index;
    private String desc;

    private ExecuteStatusEnum(int index, String desc) {
        this.index = index;
        this.desc = desc;
    }

    public static List<Map<String, Object>> parseMap() {
        List<Map<String, Object>> lists = new ArrayList<>();
        for (ExecuteStatusEnum executeStatusEnum : ExecuteStatusEnum.values()) {
            Map<String, Object> executeStatus = new HashMap<>();
            executeStatus.put("index", executeStatusEnum.getIndex());
            executeStatus.put("desc", executeStatusEnum.getDesc());
            lists.add(executeStatus);
        }
        return lists;
    }

    public static String getDesc(int index) {
        for (ExecuteStatusEnum r : ExecuteStatusEnum.values()) {
            if (r.getIndex() == index) {
                return r.desc;
            }
        }
        return null;
    }

    public static ExecuteStatusEnum getEnum(int index) {
        for (ExecuteStatusEnum r : ExecuteStatusEnum.values()) {
            if (r.getIndex() == index) {
                return r;
            }
        }
        return null;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
