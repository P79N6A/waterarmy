package com.xiaopeng.waterarmy.handle.param;

import com.xiaopeng.waterarmy.common.enums.PlatformEnum;
import com.xiaopeng.waterarmy.common.enums.TaskEntryTypeEnum;
import com.xiaopeng.waterarmy.common.enums.TaskTypeEnum;
import com.xiaopeng.waterarmy.model.dto.ProxyHttpConfig;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class RequestContext {
    /**
     * 使用的userid,本系统分配的userid
     */
    private Long userId;

    /**
     * 第三方账号登陆id
     */
    private String userLoginId;

    /**
     * 平台id
     */
    private PlatformEnum platform;

    /**
     * 处理类型，发帖，评论，点赞,浏览等
     */
    private TaskTypeEnum handleType;

    /**
     * 每个平台不同模块的分类
     */
    private TaskEntryTypeEnum handleEntryType;

    /**
     * 需要发表的内容
     */
    private Content content;

    /**
     * 需要访问的url;一般可以不设置
     */
    private String targetUrl;

    /**
     * 如果是评论等，需要爬取对应的页面，来组装数据
     */
    private String prefixUrl;


    /**
     * 针对http请求，将map中的内容全部映射到param中
     */
    private Map requestParam;

    /**
     * 针对http请求，将map中的内容全部映射到header中
     */
    private Map headerParam;

    /**
     * 图片流list
     */
    private List<InputStream> imageInputStreams;

    /**
     * 花生动态ip配置
     */
    private ProxyHttpConfig proxyHttpConfig;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserLoginId() {
        return userLoginId;
    }

    public void setUserLoginId(String userLoginId) {
        this.userLoginId = userLoginId;
    }

    public PlatformEnum getPlatform() {
        return platform;
    }

    public void setPlatform(PlatformEnum platform) {
        this.platform = platform;
    }

    public TaskTypeEnum getHandleType() {
        return handleType;
    }

    public void setHandleType(TaskTypeEnum handleType) {
        this.handleType = handleType;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getPrefixUrl() {
        return prefixUrl;
    }

    public void setPrefixUrl(String prefixUrl) {
        this.prefixUrl = prefixUrl;
    }

    public Map getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(Map requestParam) {
        this.requestParam = requestParam;
    }

    public Map getHeaderParam() {
        return headerParam;
    }

    public void setHeaderParam(Map headerParam) {
        this.headerParam = headerParam;
    }

    public TaskEntryTypeEnum getHandleEntryType() {
        return handleEntryType;
    }

    public void setHandleEntryType(TaskEntryTypeEnum handleEntryType) {
        this.handleEntryType = handleEntryType;
    }

    public List<InputStream> getImageInputStreams() {
        return imageInputStreams;
    }

    public void setImageInputStreams(List<InputStream> imageInputStreams) {
        this.imageInputStreams = imageInputStreams;
    }

    public ProxyHttpConfig getProxyHttpConfig() {
        return proxyHttpConfig;
    }

    public void setProxyHttpConfig(ProxyHttpConfig proxyHttpConfig) {
        this.proxyHttpConfig = proxyHttpConfig;
    }

    @Override
    public String toString() {
        return "RequestContext{" +
                "userId=" + userId +
                ", userLoginId='" + userLoginId + '\'' +
                ", platform=" + platform +
                ", handleType=" + handleType +
                ", handleEntryType=" + handleEntryType +
                ", content=" + content +
                ", targetUrl='" + targetUrl + '\'' +
                ", prefixUrl='" + prefixUrl + '\'' +
                ", requestParam=" + requestParam +
                ", headerParam=" + headerParam +
                '}';
    }
}
