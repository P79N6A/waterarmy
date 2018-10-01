package com.xiaopeng.waterarmy.common.enums;

/**
 * * 功能描述：账号等级枚举
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="1206401391@qq.com">iason</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/20
 */
public enum AccountLevelEnum {
    /**
     * 初级
     */
    PRIMARY (0, "初级"),

    /**
     * 中级
     */
    MIDDLE (1, "中级"),

    /**
     * 高级
     */
    ADVANCED (2, "高级");

    private int index;
    private String desc;

    private AccountLevelEnum(int index, String desc) {
        this.index = index;
        this.desc = desc;
    }

    public static String getDesc(int index) {
        for (AccountLevelEnum r : AccountLevelEnum.values()) {
            if (r.getIndex() == index) {
                return r.desc;
            }
        }
        return null;
    }

    public static AccountLevelEnum getEnum(int index) {
        for (AccountLevelEnum r : AccountLevelEnum.values()) {
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
