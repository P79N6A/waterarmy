package com.xiaopeng.waterarmy.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * * 功能描述：导入导出excel类型枚举
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="1206401391@qq.com">iason</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/20
 */
public enum ExcelDataTypeEnum {

    /**
     * 链接
     */
    LINK ("LINK", "链接"),

    /**
     * RULE
     */
    RULE ("RULE", "规则"),

    /**
     * 内容
     */
    CONTENT ("CONTENT", "内容");

    private String name;
    private String desc;

    private ExcelDataTypeEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public static List<Map<String, Object>> parseMap() {
        List<Map<String, Object>> lists = new ArrayList<>();
        for (ExcelDataTypeEnum platFormModuleEnum : ExcelDataTypeEnum.values()) {
            Map<String, Object> platFormModule = new HashMap<>();
            platFormModule.put("name", platFormModuleEnum.getName());
            platFormModule.put("desc", platFormModuleEnum.getDesc());
            lists.add(platFormModule);
        }
        return lists;
    }

    public static String getDesc(String name) {
        for (ExcelDataTypeEnum r : ExcelDataTypeEnum.values()) {
            if (r.getName().equals(name)) {
                return r.desc;
            }
        }
        return null;
    }

    public static ExcelDataTypeEnum getEnum(String name) {
        for (ExcelDataTypeEnum r : ExcelDataTypeEnum.values()) {
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
