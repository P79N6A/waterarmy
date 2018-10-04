package com.xiaopeng.waterarmy.model.dao;

import javax.persistence.Column;
import java.util.Date;

/**
 * 其实和评论的表结构一样，但是为了防止单表数据量过大，不同类型的数据通过不同的表来存储
 */
public class PublishInfo {

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

    @Column
    private String title;

    @Column
    private String body;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
