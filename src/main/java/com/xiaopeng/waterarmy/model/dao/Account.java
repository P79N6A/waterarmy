package com.xiaopeng.waterarmy.model.dao;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by iason on 2018/10/1.
 */
@Table(name = "account")
public class Account implements Serializable {

    private static final long serialVersionUID = -3867740253534460330L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户账号UUID
     */
    @Column(name = "UUID")
    private String UUID;

    /**
     * 账号名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 账号中文名
     */
    @Column(name = "full_name")
    private String fullName;

    /**
     * 账号密码
     */
    @Column(name = "password")
    private String password;

    /**
     * 手机号
     */
    @Column(name = "mobile")
    private String mobile;

    /**
     * 账号邮箱
     */
    @Column(name = "email")
    private String email;

    /**
     * 账号等级，详见AccountLevelEnum
     */
    @Column(name = "level")
    private Integer level;

    /**
     * 平台，详见PlatformEnum
     */
    @Column(name = "platform")
    private String platform;

    /**
     * 累计有效任务次数
     */
    @Column(name = "task_count")
    private Integer taskCount;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 账号状态(0 无效;1 有效)
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 创建者账号名
     */
    @Column(name = "creator")
    private String creator;

    /**
     * 更新者账号名
     */
    @Column(name = "updater")
    private String updater;

    public Account() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Integer getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

}

