package com.xiaopeng.waterarmy.handle.result;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;

public class LoginResultDTO {

    /**
     * 第三方平台userid对应的id,有些平台操作需要这个参数
     */
    private Long outId;

    /**
     * 第三方平台对应的username
     */
    private String outUserName;

    /**
     * 本系统该账户的id
     */
    private Long id;

    /**
     * 第三方平台的userId
     */
    private String outUserId;

    /**
     * 某些需要token
     */
    private String token;

    /**
     * 使用的httpClient
     */
    private CloseableHttpClient httpClient;

    /**
     *
     * @return
     */
    private BasicCookieStore cookieStore;

    public Long getOutId() {
        return outId;
    }

    public void setOutId(Long outId) {
        this.outId = outId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOutUserName() {
        return outUserName;
    }

    public void setOutUserName(String outUserName) {
        this.outUserName = outUserName;
    }

    public String getOutUserId() {
        return outUserId;
    }

    public void setOutUserId(String outUserId) {
        this.outUserId = outUserId;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public BasicCookieStore getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(BasicCookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    @Override
    public String toString() {
        return "LoginResultDTO{" +
                "outId=" + outId +
                ", outUserName='" + outUserName + '\'' +
                ", id=" + id +
                ", outUserId='" + outUserId + '\'' +
                ", token='" + token + '\'' +
                ", httpClient=" + httpClient +
                '}';
    }
}
