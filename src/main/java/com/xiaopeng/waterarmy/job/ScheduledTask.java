package com.xiaopeng.waterarmy.job;

import com.xiaopeng.waterarmy.model.dao.Account;

import java.util.List;

/**
 * * 功能描述：
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="wenlong.cwl@alibaba-inc.com">成文龙</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/11/22
 */
public interface ScheduledTask {

    /**
     * 获取可执行任务用户
     *
     * @param accounts
     * @return
     */
    Account getExecutor(List<Account> accounts);

    /**
     * 保存用户执行记录
     *
     * @param userName
     * @param platform
     * @param content
     * @param publicIP
     * @param link
     */
    void saveAccountTaskLog(String userName, String platform
            , String content, String publicIP, String link);
}
