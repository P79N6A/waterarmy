package com.xiaopeng.waterarmy.service.impl;

import com.xiaopeng.waterarmy.common.message.CodeEnum;
import com.xiaopeng.waterarmy.common.message.JsonMessage;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.mapper.AccountMapper;
import com.xiaopeng.waterarmy.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * * 功能描述：
 * <p> 版权所有：
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="1206401391@qq.com">iason</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/20
 */
@Service
public class AccountServiceImpl implements AccountService {

//    @Autowired
//    private AccountMapper accountMapper;

    @Override
    public JsonMessage getAccounts() {
        JsonMessage message = JsonMessage.init();
//        List<Account> accounts = accountMapper.getAccounts();
//        message.setData(accounts);
        message.success(CodeEnum.SUCCESS);
        return message;
    }

    @Override
    public JsonMessage getAccountByUserName(String userName) {
        JsonMessage message = JsonMessage.init();
//        Account account = accountMapper.getAccountByUserName(userName);
//        message.setData(account);
        message.success(CodeEnum.SUCCESS);
        return message;
    }

}
