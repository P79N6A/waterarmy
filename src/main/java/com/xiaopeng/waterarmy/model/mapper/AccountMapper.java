package com.xiaopeng.waterarmy.model.mapper;

import com.xiaopeng.waterarmy.common.mapper.MyMapper;
import com.xiaopeng.waterarmy.model.dao.Account;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AccountMapper extends MyMapper<Account> {

    List<Map<String, Object>> getAccounts(@Param("params") Map<String, Object> params);

    Account getAccountByUserName(@Param("userName") String userName);

    List<Account> getAccountsByPlatform(@Param("platform") String platform);

    /**
     * 通过id查询 第三方系统账号
     *
     * @param id
     * @return
     */
    Account getAccountById(@Param("id") Long id);

    void save(@Param("account") Account account);

    void update(@Param("params") Map<String, Object> params);

    void deleteById(@Param("id") Long id);

}
