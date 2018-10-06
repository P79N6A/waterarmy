package com.xiaopeng.waterarmy.model.dao;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 任务执行记录表
 *
 * Created by iason on 2018/10/1.
 */
@Table(name = "task_execute_log")
public class TaskExecuteLog implements Serializable {

    private static final long serialVersionUID = -5323840453451221L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 任务ID，对应task_info表主键
     */
    @Column(name = "task_info_id")
    private Long taskInfoId;


    /**
     * 内容ID，对应content_info表主键
     */
    @Column(name = "content_info_id")
    private Long contentInfoId;

    /**
     * 执行账号名
     */
    @Column(name = "executor")
    private String executor;

    /**
     * 任务执行状态，0 失败 1成功
     */
    @Column(name = "execute_status")
    private Integer executeStatus;

    /**
     * 执行结果返还值
     */
    @Column(name = "handler_result")
    private String handlerResult;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;


    public TaskExecuteLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskInfoId() {
        return taskInfoId;
    }

    public void setTaskInfoId(Long taskInfoId) {
        this.taskInfoId = taskInfoId;
    }

    public Long getContentInfoId() {
        return contentInfoId;
    }

    public void setContentInfoId(Long contentInfoId) {
        this.contentInfoId = contentInfoId;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public Integer getExecuteStatus() {
        return executeStatus;
    }

    public void setExecuteStatus(Integer executeStatus) {
        this.executeStatus = executeStatus;
    }

    public String getHandlerResult() {
        return handlerResult;
    }

    public void setHandlerResult(String handlerResult) {
        this.handlerResult = handlerResult;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}

