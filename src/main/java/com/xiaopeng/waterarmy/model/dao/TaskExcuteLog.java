package com.xiaopeng.waterarmy.model.dao;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 任务执行记录表
 *
 * Created by iason on 2018/10/1.
 */
@Table(name = "task_excute_log")
public class TaskExcuteLog implements Serializable {

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
     * 执行账号名
     */
    @Column(name = "executor")
    private String executor;

    /**
     * 任务执行状态，0 失败 1成功
     */
    @Column(name = "excute_status")
    private Integer excuteStatus;

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


    public TaskExcuteLog() {
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

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public Integer getExcuteStatus() {
        return excuteStatus;
    }

    public void setExcuteStatus(Integer excuteStatus) {
        this.excuteStatus = excuteStatus;
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

