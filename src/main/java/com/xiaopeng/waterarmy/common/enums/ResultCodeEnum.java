package com.xiaopeng.waterarmy.common.enums;

public enum  ResultCodeEnum {

    /**
     * 处理成功
     */
    SUCCESS (0, "成功"),

    /**
     *
     */
    USER_NOT_LOGIN(1,"该用户未登录"),


    SYSTEM_ERROR(2,"未知错误"),

    HANDLER_NOT_FOUND(3,"没有找到处理类"),

    LOGIN_FAILED(4,"登录失败"),

    INVALID_PARAM(5,"参数错误"),

    HANDLE_FAILED(6,"处理失败");

    private int index;
    private String desc;

    private ResultCodeEnum(int index, String desc) {
        this.index = index;
        this.desc = desc;
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

    public static ResultCodeEnum getEnum(int index) {
        for (ResultCodeEnum r : ResultCodeEnum.values()) {
            if (r.getIndex()==index) {
                return r;
            }
        }
        return null;
    }
}
