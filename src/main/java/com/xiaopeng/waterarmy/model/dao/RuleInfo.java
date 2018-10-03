package com.xiaopeng.waterarmy.model.dao;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by iason on 2018/10/1.
 */
@Table(name = "rule_info")
public class RuleInfo implements Serializable {

    private static final long serialVersionUID = -801254025353446476L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 是否随机选择链接，0 否 1 是
     */
    @Column(name = "is_random_select_link")
    private Integer isRandomSelectLink;

    /**
     * 是否随机选择内容，0 否 1 是
     */
    @Column(name = "is_random_select_content")
    private Integer isRandomSelectContent;

    /**
     * 开始时间间隔（随机），单位秒
     */
    @Column(name = "start_time_interval")
    private Integer startTimeInterval;

    /**
     * 开始时间间隔（随机），单位秒
     */
    @Column(name = "end_time_interval")
    private Integer endTimeInterval;

    /**
     * PV停留时间，单位秒
     */
    @Column(name = "pv_stay_time")
    private Integer pvStayTime;

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

    public RuleInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIsRandomSelectLink() {
        return isRandomSelectLink;
    }

    public void setIsRandomSelectLink(Integer isRandomSelectLink) {
        this.isRandomSelectLink = isRandomSelectLink;
    }

    public Integer getIsRandomSelectContent() {
        return isRandomSelectContent;
    }

    public void setIsRandomSelectContent(Integer isRandomSelectContent) {
        this.isRandomSelectContent = isRandomSelectContent;
    }

    public Integer getStartTimeInterval() {
        return startTimeInterval;
    }

    public void setStartTimeInterval(Integer startTimeInterval) {
        this.startTimeInterval = startTimeInterval;
    }

    public Integer getEndTimeInterval() {
        return endTimeInterval;
    }

    public void setEndTimeInterval(Integer endTimeInterval) {
        this.endTimeInterval = endTimeInterval;
    }

    public Integer getPvStayTime() {
        return pvStayTime;
    }

    public void setPvStayTime(Integer pvStayTime) {
        this.pvStayTime = pvStayTime;
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

