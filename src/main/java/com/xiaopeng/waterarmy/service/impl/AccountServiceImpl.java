package com.xiaopeng.waterarmy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaopeng.waterarmy.common.message.CodeEnum;
import com.xiaopeng.waterarmy.common.message.JsonMessage;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.mapper.AccountMapper;
import com.xiaopeng.waterarmy.service.AccountService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public PageInfo<Map<String,Object>> page(Integer pageNo, Integer pageSize, Map<String,String> params){
        PageHelper.startPage(pageNo, pageSize);
        List<Map<String,Object>> results = accountMapper.getAccounts(params);
        return new PageInfo<>(results);
    }

    @Override
    public JsonMessage getAccountByUserName(String userName) {
        JsonMessage message = JsonMessage.init();
        Account account = accountMapper.getAccountByUserName(userName);
        message.setData(account);
        message.success(CodeEnum.SUCCESS);
        return message;
    }



}
