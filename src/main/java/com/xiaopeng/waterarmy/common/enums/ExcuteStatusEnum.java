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
public enum ExcuteStatusEnum {
    /**
     * 失败
     */
    FAIL (0, "失败"),

    /**
     * 失败
     */
    SUCCEED (1, "失败");

    private int index;
    private String desc;

    private ExcuteStatusEnum(int index, String desc) {
        this.index = index;
        this.desc = desc;
    }

    public static List<Map<String, Object>> parseMap() {
        List<Map<String, Object>> lists = new ArrayList<>();
        for (ExcuteStatusEnum platformStatusEnum : ExcuteStatusEnum.values()) {
            Map<String, Object> platformStatus = new HashMap<>();
            platformStatus.put("index", platformStatusEnum.getIndex());
            platformStatus.put("desc", platformStatusEnum.getDesc());
            lists.add(platformStatus);
        }
        return lists;
    }

    public static String getDesc(int index) {
        for (ExcuteStatusEnum r : ExcuteStatusEnum.values()) {
            if (r.getIndex() == index) {
                return r.desc;
            }
        }
        return null;
    }

    public static ExcuteStatusEnum getEnum(int index) {
        for (ExcuteStatusEnum r : ExcuteStatusEnum.values()) {
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
