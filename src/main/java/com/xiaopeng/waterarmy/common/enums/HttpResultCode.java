package com.xiaopeng.waterarmy.common.enums;

public enum  HttpResultCode {

    RESULT_OK(200,"请求成功");

    private int resultCode;

    private String desc;

    HttpResultCode(int resultCode, String desc) {
        this.resultCode = resultCode;
        this.desc = desc;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
