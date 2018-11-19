package com.xiaopeng.waterarmy.handle;

import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.dto.ProxyHttpConfig;

public interface LoginHandler {

    public Result<LoginResultDTO> login(RequestContext requestContext);

    public Result<LoginResultDTO> login(Account account);

    public Result<LoginResultDTO> login(Long userid,Boolean forceToLogin);

    public void loginOut(Long userid);
}
