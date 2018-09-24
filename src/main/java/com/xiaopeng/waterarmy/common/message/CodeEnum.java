package com.xiaopeng.waterarmy.common.message;

/**
 * 状态枚举
 *
 * @author wangyi
 * @date 2017年8月21日 上午11:18:27
 */
public enum CodeEnum {

    /**
     * 通用状态码
     */
    SUCCESS("200", "请求成功"),
    FAIL("400", "请求失败");

    private String code;
    private String msg;

    private CodeEnum(String code, String name) {
        this.code = code;
        this.msg = name;
    }

    public static String getDesc(String code) {
        for (CodeEnum r : CodeEnum.values()) {
            if (r.getCode().equals(code)) {
                return r.msg;
            }
        }
        return null;
    }

    public static CodeEnum getEnum(String code) {
        for (CodeEnum r : CodeEnum.values()) {
            if (r.getCode().equals(code)) {
                return r;
            }
        }
        return null;
    }

    public String getMsg() {
        return msg;
    }

    public String getCode() {
        return code;
    }

    }