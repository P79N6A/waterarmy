package com.xiaopeng.waterarmy.model.dao;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by iason on 2018/10/1.
 */
@Table(name = "task_info")
public class TaskInfo implements Serializable {

    private static final long serialVersionUID = -5682764723534464983L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 平台，详见PlatformEnum
     */
    @Column(name = "platform")
    private String platform;

    /**
     * 模板，详见PlantFormModuleEnum
     */
    @Column(name = "module")
    private String module;

    /**
     * 任务类型，详见TaskTypeEnum
     */
    @Column(name = "task_type")
    private String taskType;

    /**
     * 可执行次数
     */
    @Column(name = "executable_count")
    private Integer executableCount;

    /**
     * 状态，详见PlatformStatus
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getExecutableCount() {
        return executableCount;
    }

    public void setExecutableCount(Integer executableCount) {
        this.executableCount = executableCount;
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
