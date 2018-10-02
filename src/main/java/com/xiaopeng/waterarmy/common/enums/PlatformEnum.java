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
public enum PlatformEnum {

//    /**
//     * 汽车之家
//     */
//    AUTOHOME ("AUTOHOME", "汽车之家"),

    /**
     * 太平洋汽车
     */
    PCAUTO ("PCAUTO", "太平洋汽车"),

    /**
     * 易车网
     */
    YICHE ("YICHE", "易车网"),

    /**
     * 爱卡汽车
     */
    XCAR ("XCAR", "爱卡汽车"),

    /**
     * 第一电动网
     */
    D1EV ("D1EV", "第一电动网"),

    /**
     * 汽车头条
     */
    QCTT ("QCTT", "汽车头条");

    private String name;
    private String desc;

    private PlatformEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public static List<Map<String, Object>> parseMap() {
        List<Map<String, Object>> lists = new ArrayList<>();
        for (PlatformEnum platformEnum : PlatformEnum.values()) {
            Map<String, Object> platform = new HashMap<>();
            platform.put("name", platformEnum.getName());
            platform.put("desc", platformEnum.getDesc());
            lists.add(platform);
        }
        return lists;
    }

    public static String getDesc(String name) {
        for (PlatformEnum r : PlatformEnum.values()) {
            if (r.getName().equals(name)) {
                return r.desc;
            }
        }
        return null;
    }

    public static PlatformEnum getEnum(String name) {
        for (PlatformEnum r : PlatformEnum.values()) {
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
