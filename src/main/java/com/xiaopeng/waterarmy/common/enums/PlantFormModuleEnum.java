package com.xiaopeng.waterarmy.common.enums;

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
public enum PlantFormModuleEnum {

    /**
     * 论坛
     */
    FORUM ("FORUM", "论坛"),

    /**
     * 新闻
     */
    NEWS ("YICHE", "新闻");

    private String name;
    private String desc;

    private PlantFormModuleEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public static String getDesc(String name) {
        for (PlantFormModuleEnum r : PlantFormModuleEnum.values()) {
            if (r.getName().equals(name)) {
                return r.desc;
            }
        }
        return null;
    }

    public static PlantFormModuleEnum getEnum(String name) {
        for (PlantFormModuleEnum r : PlantFormModuleEnum.values()) {
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
