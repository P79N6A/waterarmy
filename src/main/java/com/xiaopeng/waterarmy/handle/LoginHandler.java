package com.xiaopeng.waterarmy.handle;

import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import com.xiaopeng.waterarmy.model.dao.Account;

public interface LoginHandler {

    public Result<LoginResultDTO> login(Long userid);

    public Result<LoginResultDTO> login(Account account);

    public Result<LoginResultDTO> login(Long userid,Boolean forceToLogin);
}
