package com.xiaopeng.waterarmy.model.dao;

import javax.persistence.Column;
import java.util.Date;

public class CommentInfo {

    @Column
    private Long id;

    @Column
    private String platform;

    /**
     * 本系统该账号对应的id
     */
    @Column
    private Long userId;

    /**
     * 第三方平台user账号
     */
    @Column
    private String outUserName;

    @Column
    private Date createTime;

    @Column
    private Date modifyTime;

    @Column
    private int status;

    @Column
    private String targetUrl;

    @Column
    private String detailResult;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getOutUserName() {
        return outUserName;
    }

    public void setOutUserName(String outUserName) {
        this.outUserName = outUserName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getDetailResult() {
        return detailResult;
    }

    public void setDetailResult(String detailResult) {
        this.detailResult = detailResult;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
