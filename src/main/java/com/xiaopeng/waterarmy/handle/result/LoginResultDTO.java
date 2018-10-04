package com.xiaopeng.waterarmy.handle.result;

import org.apache.http.impl.client.CloseableHttpClient;

public class LoginResultDTO {

    /**
     * 第三方平台userid对应的id,有些平台操作需要这个参数
     */
    private Long outId;

    /**
     * 本系统该账户的id
     */
    private Long id;

    /**
     * 第三方平台的userId
     */
    private String userId;


    /**
     * 使用的httpClient
     */
    private CloseableHttpClient httpClient;


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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }
}
