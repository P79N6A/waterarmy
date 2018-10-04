package com.xiaopeng.waterarmy.common.Result;

import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;

public class Result<T> {

    private T data;

    private Boolean success;

    private int errorCode;

    private String errorMessage;

    public Result(T data, Boolean success, int errorCode, String errorMessage) {
        this.data = data;
        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public Result(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.success = false;
    }

    public Result(ResultCodeEnum resultCodeEnum) {
        this.errorMessage = resultCodeEnum.getDesc();
        this.errorCode = resultCodeEnum.getIndex();
        this.success = false;
    }

    public Result(T data) {
        this.data = data;
        this.success = true;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

