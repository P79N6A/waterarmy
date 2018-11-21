package com.xiaopeng.waterarmy.handle.impl;

import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.constants.HttpConstants;
import com.xiaopeng.waterarmy.common.enums.HttpResultCode;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.handle.LoginHandler;
import com.xiaopeng.waterarmy.handle.LoginResultPool;
import com.xiaopeng.waterarmy.handle.Util.HttpFactory;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.mapper.AccountMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QiCheTouTiaoLoginHandler implements LoginHandler {

    private static Logger logger = LoggerFactory.getLogger(QiCheTouTiaoLoginHandler.class);

    @Autowired
    private LoginResultPool loginResultPool;

    @Autowired
    private HttpFactory httpFactory;

    @Autowired
    private AccountMapper accountMapper;


    private static final String loginUrl = "https://www.qctt.cn/checklogin";

    private static final String tokenUrl = "https://www.qctt.cn/login";

    @Override
    public Result<LoginResultDTO> login(Long userid, Boolean forceToLogin) {
        return null;
    }

    @Override
    public Result<LoginResultDTO> login(RequestContext requestContext) {
        Long userid = requestContext.getUserId();
        if (userid == null || userid < 1L) {
            logger.error("[QiCheTouTiaoLoginHandler]login error; userid is null");
            return new Result<>(ResultCodeEnum.INVALID_PARAM.getIndex(), ResultCodeEnum.INVALID_PARAM.getDesc());
        }
        Account account = accountMapper.getAccountById(Long.valueOf(userid));
        account.setProxyHttpConfig(requestContext.getProxyHttpConfig());
        return this.login(account);
    }

    @Override
    public void loginOut(Long userid) {
        loginResultPool.removeLoginResult(String.valueOf(userid));
    }

    @Override
    public Result<LoginResultDTO> login(Account account) {
        if (account == null) {
            logger.error("[QiCheTouTiaoLoginHandler]login error; account is null");
            return new Result<>(ResultCodeEnum.INVALID_PARAM.getIndex(), ResultCodeEnum.INVALID_PARAM.getDesc());
        }

        String userName = account.getUserName();
        String passWord = account.getPassword();
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(passWord)) {
            logger.error("[QiCheTouTiaoLoginHandler]login error; param is invalid username is" + userName + "password is" + passWord);
            return new Result<>(ResultCodeEnum.INVALID_PARAM.getIndex(), ResultCodeEnum.INVALID_PARAM.getDesc());
        }
        LoginResultDTO loginResult = loginResultPool.getLoginResult(String.valueOf(account.getId()));
        if (loginResult != null) {
            return new Result<>(loginResult);
        }
        CloseableHttpClient httpClient = httpFactory.getHttpClient();
        HttpPost httpPost = new HttpPost(loginUrl);
        setHeader(httpPost);
        String token = this.getToken(tokenUrl,httpClient);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("username", userName));
        nameValuePairs.add(new BasicNameValuePair("password", passWord));
        nameValuePairs.add(new BasicNameValuePair("type", "10"));
        nameValuePairs.add(new BasicNameValuePair("remember","1"));
        nameValuePairs.add(new BasicNameValuePair("_token", token));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            if (response.getStatusLine().getStatusCode() != HttpResultCode.RESULT_OK.getResultCode()) {
                logger.error("[QiCheTouTiaoLoginHandler.login] UrlEncodedFormEntity login failed! account is " + account);
                return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
            }
            if ("1".equals(content)) {//登陆成功
                LoginResultDTO loginResultDTO = new LoginResultDTO();
                loginResultDTO.setHttpClient(httpClient);
                loginResultDTO.setId(account.getId());
                loginResultDTO.setOutUserId(account.getUserName());
                loginResultDTO.setToken(token);
                loginResultPool.putToLoginResultMap(String.valueOf(account.getId()), loginResultDTO);
                return new Result<>(loginResultDTO);
            }
        } catch (Exception e) {
            logger.error("[QiCheTouTiaoLoginHandler.login] UrlEncodedFormEntity error! account is " + account, e);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }
        return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
    }



    private String getToken(String url,CloseableHttpClient httpClient) {
        String token = null;
        //获取元素的时候重试三次
        int count = 3;
        while (count>0) {
            try {
                HttpGet httpGet = new HttpGet(url);
                setHeader(httpGet);
                CloseableHttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                String content = EntityUtils.toString(entity, "utf-8");
                Document doc = Jsoup.parse(content);
                Elements elements = doc.select("[name=_token]") ;
                token = elements.get(0).val();
                break;
            }catch (Exception e) {
                logger.error("[QiCheTouTiaoLoginHandler.getToken] error!",e);
            }
        }
        return token;
    }

    private void setHeader(HttpPost httpPost) {
        httpPost.setHeader("origin","https://www.qctt.cn");
        httpPost.setHeader("referer","https://www.qctt.cn/login");
        httpPost.setHeader("user-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        httpPost.setHeader("x-requested-with","XMLHttpRequest");
    }

    private void setHeader(HttpGet httpGet) {
        httpGet.setHeader("origin","https://www.qctt.cn");
        httpGet.setHeader("referer","https://www.qctt.cn/login");
        httpGet.setHeader("user-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        httpGet.setHeader("x-requested-with","XMLHttpRequest");
    }

}

