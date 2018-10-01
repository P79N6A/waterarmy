package com.xiaopeng.waterarmy.model.mapper;

import com.xiaopeng.waterarmy.common.mapper.MyMapper;
import com.xiaopeng.waterarmy.model.dao.Account;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccountMapper extends MyMapper<Account> {

    List<Account> getAccounts();

    Account getAccountByUserName(@Param("userName") String userName);

}
