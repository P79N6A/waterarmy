package com.xiaopeng.waterarmy.handle.result;

/**
 *
 * 汽车之家评论返回dto
 *
 */
public class AutoHomeCommentResultDTO {

    private boolean fakeSucceed;
    private boolean succeed;
    private String errMsg;
    private String errMap;
    private String topic;
    private String topicId;
    private String bbs;
    private String bbsid;
    private String loginId;
    private String currUser;
    private String loginName;
    private String content;
    private String newFloor;
    private String clientIP;
    private String addMoney;
    private String replyId;
    private String redirect;
    private String redirectPageIndex;
    private String alertMsg;
    private String Antiflood;

    public boolean isFakeSucceed() {
        return fakeSucceed;
    }

    public void setFakeSucceed(boolean fakeSucceed) {
        this.fakeSucceed = fakeSucceed;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrMap() {
        return errMap;
    }

    public void setErrMap(String errMap) {
        this.errMap = errMap;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getBbs() {
        return bbs;
    }

    public void setBbs(String bbs) {
        this.bbs = bbs;
    }

    public String getBbsid() {
        return bbsid;
    }

    public void setBbsid(String bbsid) {
        this.bbsid = bbsid;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getCurrUser() {
        return currUser;
    }

    public void setCurrUser(String currUser) {
        this.currUser = currUser;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNewFloor() {
        return newFloor;
    }

    public void setNewFloor(String newFloor) {
        this.newFloor = newFloor;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public String getAddMoney() {
        return addMoney;
    }

    public void setAddMoney(String addMoney) {
        this.addMoney = addMoney;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getRedirectPageIndex() {
        return redirectPageIndex;
    }

    public void setRedirectPageIndex(String redirectPageIndex) {
        this.redirectPageIndex = redirectPageIndex;
    }

    public String getAlertMsg() {
        return alertMsg;
    }

    public void setAlertMsg(String alertMsg) {
        this.alertMsg = alertMsg;
    }

    public String getAntiflood() {
        return Antiflood;
    }

    public void setAntiflood(String antiflood) {
        Antiflood = antiflood;
    }
}
