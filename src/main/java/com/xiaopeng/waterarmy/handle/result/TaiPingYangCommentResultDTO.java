package com.xiaopeng.waterarmy.handle.result;

import org.apache.commons.lang3.StringUtils;

public class TaiPingYangCommentResultDTO {

    //{"message":"天还未冷，尚有余热，鸡皮已上身","need_captcha":false,"pid":156974128,"qid":0,"status":0,"tid":17473908,"userId":47446648,"userName":"eiaxal8142"}

    //提交过快，会被阻止
    //{"desc":"请不要重复提交","status":-1}
    private String message;

    private Boolean need_captcha;

    private String pid;

    private String qid;

    private String status;

    private String tid;

    private String userId;

    private String userName;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getNeed_captcha() {
        return need_captcha;
    }

    public void setNeed_captcha(Boolean need_captcha) {
        this.need_captcha = need_captcha;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
