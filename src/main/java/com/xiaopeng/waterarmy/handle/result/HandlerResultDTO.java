package com.xiaopeng.waterarmy.handle.result;


import com.xiaopeng.waterarmy.common.enums.PlatformEnum;
import com.xiaopeng.waterarmy.common.enums.TaskTypeEnum;

public class HandlerResultDTO {

    /**
     * 使用的userid,本系统分配的userid
     */
    private Long userId;

    /**
     * 第三方账号登陆id
     */
    private String userLoginId;

    /**
     * 平台
     */
    private PlatformEnum platform;

    /**
     * 处理类型，发帖，评论，点赞等
     */
    private TaskTypeEnum handleType;

    /**
     * 处理完成后返回的详细结果，一般都为json字符串
     */
    private String detailResult;


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

    public String getDetailResult() {
        return detailResult;
    }

    public void setDetailResult(String detailResult) {
        this.detailResult = detailResult;
    }
}
