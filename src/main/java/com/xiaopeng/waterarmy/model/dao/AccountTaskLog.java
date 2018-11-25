package com.xiaopeng.waterarmy.model.dao;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "account_task_log")
public class AccountTaskLog implements Serializable {

    private static final long serialVersionUID = -1275740253534460523L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 账号名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 平台，详见PlatformEnum
     */
    @Column(name = "platform")
    private String platform;

    /**
     * 任务类型，详见TaskTypeEnum
     */
    @Column(name = "task_type")
    private String taskType;

    /**
     * 任务链接(发帖，评论，评论点赞)
     */
    @Column(name = "link")
    private String link;

    /**
     * 内容(评论，发帖)
     */
    @Column(name = "content")
    private String content;

    /**
     * ip
     */
    @Column(name = "ip")
    private String ip;

    /**
     * 任务执行时间
     */
    @Column(name = "create_time")
    private Date createTime;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}