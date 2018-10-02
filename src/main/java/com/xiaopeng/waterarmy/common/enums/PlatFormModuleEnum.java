package com.xiaopeng.waterarmy.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * * 功能描述：模块枚举
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="1206401391@qq.com">iason</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/20
 */
public enum PlatFormModuleEnum {

    /**
     * 论坛
     */
    FORUM ("FORUM", "论坛"),

    /**
     * 新闻
     */
    NEWS ("NEWS", "新闻"),

    /**
     * 车家号
     */
    CHEJIAHAO ("CHEJIAHAO", "车家号"),

    /**
     * 口碑
     */
    KOUBEI ("KOUBEI", "口碑"),;

    private String name;
    private String desc;

    private PlatFormModuleEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public static List<Map<String, Object>> parseMap() {
        List<Map<String, Object>> lists = new ArrayList<>();
        for (PlatFormModuleEnum platFormModuleEnum : PlatFormModuleEnum.values()) {
            Map<String, Object> platFormModule = new HashMap<>();
            platFormModule.put("name", platFormModuleEnum.getName());
            platFormModule.put("desc", platFormModuleEnum.getDesc());
            lists.add(platFormModule);
        }
        return lists;
    }

    public static String getDesc(String name) {
        for (PlatFormModuleEnum r : PlatFormModuleEnum.values()) {
            if (r.getName().equals(name)) {
                return r.desc;
            }
        }
        return null;
    }

    public static PlatFormModuleEnum getEnum(String name) {
        for (PlatFormModuleEnum r : PlatFormModuleEnum.values()) {
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
