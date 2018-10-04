package com.xiaopeng.waterarmy.handle.impl;

import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.enums.HttpResultCode;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.handle.LoginHandler;
import com.xiaopeng.waterarmy.handle.LoginResultPool;
import com.xiaopeng.waterarmy.handle.Util.HttpFactory;
import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.mapper.AccountMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AiKaLoginHandler implements LoginHandler {

    private static Logger logger = LoggerFactory.getLogger(AiKaLoginHandler.class);

    public static final String UserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";

    public static final String Host = "reg.xcar.com.cn";

    public static final String Origin = "http://www.xcar.com.cn";

    private static final String loginUrl = "http://reg.xcar.com.cn/ajax/ajax_dologin.php";


    @Autowired
    private LoginResultPool loginResultPool;

    @Autowired
    private HttpFactory httpFactory;

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public Result<LoginResultDTO> login(Long userid) {

        if (userid == null || userid < 1L) {
            logger.error("[TaiPingYangLoginHandler]login error; userid is null");
            return new Result<>(ResultCodeEnum.INVALID_PARAM.getIndex(), ResultCodeEnum.INVALID_PARAM.getDesc());
        }
        Account account = accountMapper.getAccountById(Long.valueOf(userid));
        return this.login(account);
    }

    /**
     * logintype: 2
     * username: 2222222222
     * userpwd: 777cac5d6f1387dec2965c69a9dbe9e9
     * u:
     * checkcode: 请输入验证码
     * @param account
     * @return
     */

    @Override
    public Result<LoginResultDTO> login(Account account) {
        if (account == null) {
            logger.error("[AiKaLoginHandler]login error; account is null");
            return new Result<>(ResultCodeEnum.INVALID_PARAM.getIndex(), ResultCodeEnum.INVALID_PARAM.getDesc());
        }

        String userName = account.getUserName();
        String passWord = account.getPassword();
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(passWord)) {
            logger.error("[AiKaLoginHandler]login error; param is invalid username is" + userName + "password is" + passWord);
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
        nameValuePairs.add(new BasicNameValuePair("username", userName));
        nameValuePairs.add(new BasicNameValuePair("userpwd", DigestUtils.md5Hex(passWord)));
        nameValuePairs.add(new BasicNameValuePair("logintype", "2"));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != HttpResultCode.RESULT_OK.getResultCode()) {
                logger.error("[AiKaLoginHandler.login] UrlEncodedFormEntity login failed! account is " + account);
                return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
            }
            /* HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            System.out.println(content);
            System.out.println(httpPost.getURI());
            System.out.println(response);*/
        } catch (Exception e) {
            logger.error("[AiKaLoginHandler.login] UrlEncodedFormEntity error! account is " + account, e);
        }
        LoginResultDTO loginResultDTO = new LoginResultDTO();
        loginResultDTO.setHttpClient(httpClient);
        loginResultPool.putToLoginResultMap(account.getUserName(), loginResultDTO);
        return new Result<>(loginResultDTO);
    }

    private void setHeader(HttpPost httpPost) {
        httpPost.setHeader("User-Agent", UserAgent);
        httpPost.setHeader("Origin", Origin);
        httpPost.setHeader("Host", Host);
    }
}
