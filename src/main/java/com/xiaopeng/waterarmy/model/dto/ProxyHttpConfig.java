package com.xiaopeng.waterarmy.model.dto;

import org.apache.http.client.config.RequestConfig;

/**
 * Created by iason on 2018/11/19.
 */
public class ProxyHttpConfig {

    private RequestConfig reqConfig;
    // 代理服务器
    private String proxyHost;
    private Integer proxyPort;
    private boolean isUsed;

    public RequestConfig getReqConfig() {
        return reqConfig;
    }

    public void setReqConfig(RequestConfig reqConfig) {
        this.reqConfig = reqConfig;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

}
