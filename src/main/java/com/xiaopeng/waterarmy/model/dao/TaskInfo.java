package com.xiaopeng.waterarmy.model.dao;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 任务信息表
 *
 * Created by iason on 2018/10/1.
 */
@Table(name = "task_info")
public class TaskInfo implements Serializable {

    private static final long serialVersionUID = -5682764723534464983L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 发布任务ID，对应task_publish表主键
     */
    @Column(name = "task_publish_id")
    private Long taskPublishId;

    /**
     * 规则ID
     */
    @Column(name = "rule_info_id")
    private Long ruleInfoId;

    /**
     * 任务名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 平台，详见PlatformEnum
     */
    @Column(name = "platform")
    private String platform;

    /**
     * 模板，详见PlatFormModuleEnum
     */
    @Column(name = "module")
    private String module;

    /**
     * 任务类型，详见TaskTypeEnum
     */
    @Column(name = "task_type")
    private String taskType;

    /**
     * 执行任务链接
     */
    @Column(name = "link")
    private String link;

    /**
     * 链接标题
     */
    @Column(name = "link_title")
    private String linkTitle;

    /**
     * 需要点赞内容
     */
    @Column(name = "like_content")
    private String likeContent;

    /**
     * 内容库类型，详见ContentRepositoriesEnum
     */
    @Column(name = "content_repositories_type")
    private String contentRepositoriesType;

    /**
     * 内容库名称
     */
    @Column(name = "content_repositories_name")
    private String contentRepositoriesName;

    /**
     * 执行数量
     */
    @Column(name = "execute_count")
    private Integer executeCount;

    /**
     * 完成数量
     */
    @Column(name = "finish_count")
    private Integer finishCount;

    /**
     * 任务完成时间
     */
    @Column(name = "finish_time")
    private Date finishTime;

    /**
     * 任务状态，详见TaskStatusEnum
     */
    @Column(name = "status")
    private Integer status;

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
     * 创建者账号名
     */
    @Column(name = "creator")
    private String creator;

    /**
     * 更新者账号名
     */
    @Column(name = "updater")
    private String updater;

    public TaskInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskPublishId() {
        return taskPublishId;
    }

    public void setTaskPublishId(Long taskPublishId) {
        this.taskPublishId = taskPublishId;
    }

    public Long getRuleInfoId() {
        return ruleInfoId;
    }

    public void setRuleInfoId(Long ruleInfoId) {
        this.ruleInfoId = ruleInfoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
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

    public String getLinkTitle() {
        return linkTitle;
    }

    public void setLinkTitle(String linkTitle) {
        this.linkTitle = linkTitle;
    }

    public String getLikeContent() {
        return likeContent;
    }

    public void setLikeContent(String likeContent) {
        this.likeContent = likeContent;
    }

    public String getContentRepositoriesType() {
        return contentRepositoriesType;
    }

    public void setContentRepositoriesType(String contentRepositoriesType) {
        this.contentRepositoriesType = contentRepositoriesType;
    }

    public String getContentRepositoriesName() {
        return contentRepositoriesName;
    }

    public void setContentRepositoriesName(String contentRepositoriesName) {
        this.contentRepositoriesName = contentRepositoriesName;
    }

    public Integer getExecuteCount() {
        return executeCount;
    }

    public void setExecuteCount(Integer executeCount) {
        this.executeCount = executeCount;
    }

    public Integer getFinishCount() {
        return finishCount;
    }

    public void setFinishCount(Integer finishCount) {
        this.finishCount = finishCount;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
