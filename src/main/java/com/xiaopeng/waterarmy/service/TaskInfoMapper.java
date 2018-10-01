package com.xiaopeng.waterarmy.service;

import com.xiaopeng.waterarmy.common.mapper.MyMapper;
import com.xiaopeng.waterarmy.model.dao.Account;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TaskInfoMapper extends MyMapper<Account> {

    List<Map<String,Object>> getAccounts(@Param("params") Map<String, String> params);

    Account getAccountByUserName(@Param("userName") String userName);

}
