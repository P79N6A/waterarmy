package com.xiaopeng.waterarmy.service;

import com.github.pagehelper.PageInfo;
import com.xiaopeng.waterarmy.common.message.JsonMessage;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.dao.AccountIPInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * * 功能描述：
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="1206401391@qq.com">iason</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/20
 */
public interface AccountService {

    /**
     * 账号信息分页查询
     *
     * @param pageNo
     * @param pageSize
     * @param params
     * @return
     */
    PageInfo<Map<String,Object>> page(Integer pageNo, Integer pageSize, Map<String,Object> params);

    List<Account> getAccountsByPlatform(String platform);

    Account getAccountByUserName(String userName);

    JsonMessage add(Map<String,Object> params);

    JsonMessage update(Map<String,Object> params);

    JsonMessage delete(String id);

    JsonMessage importData(MultipartFile file);

    JsonMessage updateTaskCount(String userName);

    Map<String, Object> getAccountIPInfo(String ip, String platform, String userName);

    boolean saveAccountIPInfo(AccountIPInfo accountIPInfo);

}