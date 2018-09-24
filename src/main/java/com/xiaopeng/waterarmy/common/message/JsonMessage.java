package com.xiaopeng.waterarmy.common.message;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;

public class JsonMessage extends JSONObject implements Serializable {

    private static final long serialVersionUID = -4891861117747607649L;
    private Boolean success;
    private String status;
    private String code;
    private String msg;
    private Object data;
    private Long total;

    private JsonMessage() {
    }

    public JsonMessage(CodeEnum code) {
        this.status = code.getCode();
        this.msg = code.getMsg();
        this.put("status", this.status);
        this.put("msg", this.msg);
        this.put("code", this.status);
    }

    public static JsonMessage init() {
        JsonMessage result = new JsonMessage();
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFail() {
        if (ObjectUtils.isEmpty(success)) {
            return false;
        }
        return !success;
    }

    public JsonMessage data(Object data) {
        this.data = data;
        this.put("data", this.data);
        return this;
    }

    /**
     * @param codeEnum 状态枚举
     * @return
     * @desc: 成功
     * @author： wangyi
     * @date： 2017年8月18日 下午3:33:14
     */
    public JsonMessage success(CodeEnum codeEnum) {
        this.success = true;
        this.status = codeEnum.getCode();
        this.msg = codeEnum.getMsg();
        this.put("success", this.success);
        this.put("status", this.status);
        this.put("msg", this.msg);
        this.put("code", this.status);
        return this;
    }

    /**
     * @param codeEnum 状态枚举
     * @param msg      消息体
     * @param args     消息动态遍历
     * @return
     * @desc: 成功
     * @author： wangyi
     * @date： 2017年8月18日 下午3:33:51
     */
    public JsonMessage success(CodeEnum codeEnum, String msg, Object... args) {
        this.success = true;
        this.status = codeEnum.getCode();
        this.msg = String.format(msg, args);
        this.put("success", this.success);
        this.put("status", this.status);
        this.put("msg", this.msg);
        this.put("code", this.status);
        return this;
    }

    /**
     * @param codeEnum 状态枚举
     * @return
     * @desc: 失败
     * @author： wangyi
     * @date： 2017年8月18日 下午3:33:51
     */
    public JsonMessage fail(CodeEnum codeEnum) {
        this.success = false;
        this.status = codeEnum.getCode();
        this.msg = codeEnum.getMsg();
        this.put("success", this.success);
        this.put("status", this.status);
        this.put("msg", this.msg);
        this.put("code", this.status);
        return this;
    }

    /**
     * @param codeEnum 状态枚举
     * @param msg      消息体
     * @param args     消息动态遍历
     * @return
     * @desc: 失败
     * @author： wangyi
     * @date： 2017年8月18日 下午3:33:51
     */
    public JsonMessage fail(CodeEnum codeEnum, String msg, Object... args) {
        this.success = false;
        this.status = codeEnum.getCode();
        this.msg = String.format(msg, args);
        this.put("success", this.success);
        this.put("status", this.status);
        this.put("msg", this.msg);
        this.put("code", this.status);
        return this;
    }

    /*********************get set********************/
    public Boolean getSuccess() {
        return success;
    }

    public JsonMessage setSuccess(Boolean success) {
        this.success = success;
        this.put("success", this.success);
        return this;
    }

    public String getStatus() {
        return status;
    }

    public JsonMessage setStatus(String status) {
        this.status = status;
        this.put("status", this.status);
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public JsonMessage setMsg(String msg) {
        this.msg = msg;
        this.put("msg", this.msg);
        return this;
    }

    public JsonMessage setMsg(String msg, Object... params) {
        this.msg = String.format(msg, params);
        this.put("msg", this.msg);
        return this;
    }

    public Object getData() {
        return data;
    }

    public JsonMessage setData(Object data) {
        this.data = data;
        this.put("data", this.data);
        return this;
    }

    public Long getTotal() {
        return total;
    }

    public JsonMessage setTotal(Long total) {
        this.total = total;
        this.put("total", this.total);
        return this;
    }

    public String getCode() {
        return code;
    }

    public JsonMessage setCode(String code) {
        this.code = code;
        this.put("code", this.code);
        return this;
    }

}