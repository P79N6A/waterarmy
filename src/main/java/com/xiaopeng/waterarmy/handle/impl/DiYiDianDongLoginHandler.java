package com.xiaopeng.waterarmy.handle.impl;

import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.handle.LoginHandler;
import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import com.xiaopeng.waterarmy.model.dao.Account;

public class DiYiDianDongLoginHandler implements LoginHandler {
    @Override
    public Result<LoginResultDTO> login(Long userid) {
        return null;
    }

    @Override
    public Result<LoginResultDTO> login(Account account) {
        return null;
    }
}
