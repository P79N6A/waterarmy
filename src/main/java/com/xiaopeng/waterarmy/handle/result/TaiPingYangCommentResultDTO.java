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

    private Long commentId;

    private Integer resultCode;

    private Integer code;

    private String showName;

    private String brief;

    private Long topicId;

    private Integer floor;

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

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }
}
