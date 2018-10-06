package com.xiaopeng.waterarmy.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 是否随机选择内容，0 否 1 是
 * Created by iason on 2018/10/5.
 */
public enum RandomSelectContentEnum {
    /**
     * 否
     */
    YES(0, "否"),

    /**
     * 是
     */
    NO(1, "是");

    private int index;
    private String desc;

    private RandomSelectContentEnum(int index, String desc) {
        this.index = index;
        this.desc = desc;
    }

    public static List<Map<String, Object>> parseMap() {
        List<Map<String, Object>> lists = new ArrayList<>();
        for (RandomSelectContentEnum randomSelectContentEnum : RandomSelectContentEnum.values()) {
            Map<String, Object> randomSelectContents = new HashMap<>();
            randomSelectContents.put("index", randomSelectContentEnum.getIndex());
            randomSelectContents.put("desc", randomSelectContentEnum.getDesc());
            lists.add(randomSelectContents);
        }
        return lists;
    }

    public static String getDesc(int index) {
        for (RandomSelectContentEnum r : RandomSelectContentEnum.values()) {
            if (r.getIndex() == index) {
                return r.desc;
            }
        }
        return null;
    }

    public static RandomSelectContentEnum getEnum(int index) {
        for (RandomSelectContentEnum r : RandomSelectContentEnum.values()) {
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
