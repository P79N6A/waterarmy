package com.xiaopeng.waterarmy.handle.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.enums.HttpResultCode;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.handle.LoginHandler;
import com.xiaopeng.waterarmy.handle.LoginResultPool;
import com.xiaopeng.waterarmy.handle.Util.HttpFactory;
import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.mapper.AccountMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DiYiDianDongLoginHandler implements LoginHandler {

    private static Logger logger = LoggerFactory.getLogger(DiYiDianDongLoginHandler.class);

    private static final String loginUrl = "https://www.d1ev.com/account/login/login_check";

    @Autowired
    private LoginResultPool loginResultPool;

    @Autowired
    private HttpFactory httpFactory;

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public Result<LoginResultDTO> login(Long userid, Boolean forceToLogin) {
        return null;
    }

    @Override
    public Result<LoginResultDTO> login(Long userid) {
        if (userid == null || userid < 1L) {
            logger.error("[DiYiDianDongLoginHandler]login error; userid is null");
            return new Result<>(ResultCodeEnum.INVALID_PARAM.getIndex(), ResultCodeEnum.INVALID_PARAM.getDesc());
        }
        Account account = accountMapper.getAccountById(Long.valueOf(userid));
        return this.login(account);
    }

    @Override
    public Result<LoginResultDTO> login(Account account) {
        if (account == null) {
            logger.error("[DiYiDianDongLoginHandler]login error; account is null");
            return new Result<>(ResultCodeEnum.INVALID_PARAM.getIndex(), ResultCodeEnum.INVALID_PARAM.getDesc());
        }

        String userName = account.getUserName();
        String passWord = account.getPassword();
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(passWord)) {
            logger.error("[DiYiDianDongLoginHandler]login error; param is invalid username is" + userName + "password is" + passWord);
            return new Result<>(ResultCodeEnum.INVALID_PARAM.getIndex(), ResultCodeEnum.INVALID_PARAM.getDesc());
        }
        LoginResultDTO loginResult = loginResultPool.getLoginResult(account.getUserName());
        if (loginResult != null) {
            return new Result<>(loginResult);
        }
        CloseableHttpClient httpClient = httpFactory.getHttpClient();
        HttpPost httpPost = new HttpPost(loginUrl);
        setHeader(httpPost);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("loginName", userName));
        nameValuePairs.add(new BasicNameValuePair("password", passWord));
        nameValuePairs.add(new BasicNameValuePair("url", "https://www.d1ev.com/"));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != HttpResultCode.RESULT_OK.getResultCode()) {
                logger.error("[DiYiDianDongLoginHandler.login] UrlEncodedFormEntity login failed! account is " + account);
                return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
            }
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            JSONObject jsonObject = JSONObject.parseObject(content);
            int stauts = (int)jsonObject.get("error");
            if (stauts == 0) {
                LoginResultDTO loginResultDTO = new LoginResultDTO();
                loginResultDTO.setHttpClient(httpClient);
                loginResultDTO.setId(account.getId());
                loginResultDTO.setUserId(account.getUserName());
                loginResultDTO.setHttpClient(httpClient);
                loginResultPool.putToLoginResultMap(account.getUserName(), loginResultDTO);
                return new Result<>(loginResultDTO);
            }
        } catch (Exception e) {
            logger.error("[DiYiDianDongLoginHandler.login] UrlEncodedFormEntity error! account is " + account, e);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }
        return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
    }

    private void setHeader(HttpPost httpPost) {
        httpPost.setHeader("Origin", "https://www.d1ev.com");
        httpPost.setHeader("Referer", "https://www.d1ev.com/account/login");
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
        httpPost.setHeader("Host", "www.d1ev.com");
    }
}
