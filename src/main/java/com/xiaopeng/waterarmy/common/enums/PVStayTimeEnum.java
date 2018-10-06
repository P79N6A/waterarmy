package com.xiaopeng.waterarmy.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * * 功能描述：PV停留时间枚举
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="1206401391@qq.com">iason</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/20
 */
public enum PVStayTimeEnum {
    /**
     * 0-10秒
     */
    stay0 (0, "0-10秒随机"),

    /**
     * 10-20秒随机
     */
    stay1 (1, "10-20秒随机"),

    /**
     * 20-30秒随机
     */
    stay2 (2, "20-30秒随机"),

    /**
     * 30-40秒随机
     */
    stay3 (3, "30-40秒随机"),

    /**
     * 40-50秒随机
     */
    stay4 (4, "40-50秒随机"),

    /**
     * 50-60秒随机
     */
    stay5 (5, "50-60秒随机");

    private int index;
    private String desc;

    private PVStayTimeEnum(int index, String desc) {
        this.index = index;
        this.desc = desc;
    }

    public static String getDesc(int index) {
        for (PVStayTimeEnum r : PVStayTimeEnum.values()) {
            if (r.getIndex() == index) {
                return r.desc;
            }
        }
        return null;
    }

    public static PVStayTimeEnum getEnum(int index) {
        for (PVStayTimeEnum r : PVStayTimeEnum.values()) {
            if (r.getIndex() == index) {
                return r;
            }
        }
        return null;
    }

    public static List<Map<String, Object>> parseMap() {
        List<Map<String, Object>> lists = new ArrayList<>();
        for (PVStayTimeEnum pvStayTimeEnum : PVStayTimeEnum.values()) {
            Map<String, Object> pvStayTime = new HashMap<>();
            pvStayTime.put("index", pvStayTimeEnum.getIndex());
            pvStayTime.put("desc", pvStayTimeEnum.getDesc());
            lists.add(pvStayTime);
        }
        return lists;
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
