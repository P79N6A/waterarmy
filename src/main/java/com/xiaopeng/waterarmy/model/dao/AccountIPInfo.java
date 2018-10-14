package com.xiaopeng.waterarmy.model.dao;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户与平台，ip绑定表
 *
 * Created by iason on 2018/10/1.
 */
@Table(name = "account_ip_info")
public class AccountIPInfo implements Serializable {

    private static final long serialVersionUID = -1354740253534460122L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 账号名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * ip
     */
    @Column(name = "ip")
    private String ip;

    /**
     * 平台，详见PlatformEnum
     */
    @Column(name = "platform")
    private String platform;

    public AccountIPInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

}

