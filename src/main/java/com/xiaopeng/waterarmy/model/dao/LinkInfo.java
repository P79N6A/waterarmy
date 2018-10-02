package com.xiaopeng.waterarmy.model.dao;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by iason on 2018/10/1.
 */
@Table(name = "account")
public class LinkInfo implements Serializable {

    private static final long serialVersionUID = -4267840253534461673L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 任务ID，对应task_info表主键
     */
    @Column(name = "task_info_id")
    private Long taskInfoId;

    /**
     * 名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 链接
     */
    @Column(name = "link")
    private String link;

    /**
     * 数量
     */
    @Column(name = "count")
    private Integer count;

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

    public LinkInfo() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
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

