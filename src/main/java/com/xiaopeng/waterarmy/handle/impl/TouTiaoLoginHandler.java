package com.xiaopeng.waterarmy.handle.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.enums.FeiFeiTypeEnum;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.handle.LoginHandler;
import com.xiaopeng.waterarmy.handle.LoginResultPool;
import com.xiaopeng.waterarmy.handle.Util.HttpFactory;
import com.xiaopeng.waterarmy.handle.Util.TranslateCodeUtil;
import com.xiaopeng.waterarmy.handle.Util.Util;
import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.mapper.AccountMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class TouTiaoLoginHandler implements LoginHandler {

    private static Logger logger = LoggerFactory.getLogger(TouTiaoLoginHandler.class);

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
            logger.error("[TouTiaoLoginHandler]login error; userid is null");
            return new Result<>(ResultCodeEnum.INVALID_PARAM.getIndex(), ResultCodeEnum.INVALID_PARAM.getDesc());
        }
        Account account = accountMapper.getAccountById(Long.valueOf(userid));
        return this.login(account);
    }

    @Override
    public Result<LoginResultDTO> login(Account account) {
        if (account == null) {
            logger.error("[AiKaLoginHandler]login error; account is null");
            return new Result<>(ResultCodeEnum.INVALID_PARAM.getIndex(), ResultCodeEnum.INVALID_PARAM.getDesc());
        }

        //今日头条需要加mobile，如果账户都是手机号码则与username相同，这里预留，暂时都保持与userName相同
        String mobile = account.getUserName();
        String userName = account.getUserName();
        String passWord = account.getPassword();
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(passWord)) {
            logger.error("[AiKaLoginHandler]login error; param is invalid username is" + userName + "password is" + passWord);
            return new Result<>(ResultCodeEnum.INVALID_PARAM.getIndex(), ResultCodeEnum.INVALID_PARAM.getDesc());
        }
        LoginResultDTO loginResult = loginResultPool.getLoginResult(String.valueOf(account.getId()));
        if (loginResult != null) {
            return new Result<>(loginResult);
        }

        CloseableHttpClient httpClient = null;

        int retry = 5;
        while (retry > 0) {
            retry--;

            /**
             * mobile: 15669032295
             * code:
             * account: 15669032295
             * password: qwertyui123HXJ
             * captcha: wqes
             * is_30_days_no_login: false
             * service: https://www.toutiao.com/
             */

            try {
                BasicCookieStore cookieStore = new BasicCookieStore();
                httpClient = httpFactory.getHttpClientWithCookies(cookieStore);
                //获取request对应的cookie
//                String pageUrl = "https://www.toutiao.com/";
//                HttpGet pageHttpGet = new HttpGet(pageUrl);
//                setToutiaoHeader(pageHttpGet);
//                httpClient.execute(pageHttpGet);
//
//
//                String url = "https://sso.toutiao.com/";
//                HttpGet httpGet = new HttpGet(url);
//                setSSOHeader(httpGet);
//                httpClient.execute(httpGet);

                String codeUrl = "https://sso.toutiao.com/refresh_captcha/";
                HttpGet httpGetCode = new HttpGet(codeUrl);
                setToutiaoHeader(httpGetCode);
                CloseableHttpResponse response = httpClient.execute(httpGetCode);
                HttpEntity entity = response.getEntity();
                String content = EntityUtils.toString(entity, "utf-8");
                JSONObject jsonObject =JSONObject.parseObject(content);
                String json = (String) jsonObject.get("captcha");
                //获取验证码
                Util.HttpResp resp = null;

                resp = TranslateCodeUtil.getInstance().convertByFeiFeiWithBase64(json, FeiFeiTypeEnum.FOUR_WORDS_NUMBERS.getName());
                String code = resp.pred_resl;


                //mobile: 15669032295
                //code:
                //account: 15669032295
                //password: qwertyui123HXJ
                //captcha: wqes
                //is_30_days_no_login: false
                //service: https://www.toutiao.com/
                String loginUrl = "https://sso.toutiao.com/account_login/";
                HttpPost httpPost = new HttpPost(loginUrl);
                setLoginHeader(httpPost);



                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("mobile", mobile));
                nameValuePairs.add(new BasicNameValuePair("account", passWord));
                nameValuePairs.add(new BasicNameValuePair("captcha", code));
                nameValuePairs.add(new BasicNameValuePair("is_30_days_no_login", "false"));
                nameValuePairs.add(new BasicNameValuePair("service", "https://www.toutiao.com/"));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                CloseableHttpResponse response1 = httpClient.execute(httpPost);
                String content1 = EntityUtils.toString(response1.getEntity(), "utf-8");

                if (content1.contains("secques")) {
                    LoginResultDTO loginResultDTO = new LoginResultDTO();
                    loginResultDTO.setHttpClient(httpClient);
                    loginResultPool.putToLoginResultMap(String.valueOf(account.getId()), loginResultDTO);
                    return new Result<>(loginResultDTO);
                }
                Thread.sleep(800);
            } catch (Exception e) {
                logger.error("[AiKaLoginHandler.login] UrlEncodedFormEntity error! account is " + account, e);
            }
        }
        try {
            httpClient.close();
        } catch (Exception e) {
            logger.error(" login error!",e);
        }
        return new Result(ResultCodeEnum.LOGIN_FAILED);
    }



    private void setSSOHeader(HttpGet httpGet) {
        //Host: sso.toutiao.com
        //Referer: https://sso.toutiao.com/
        //User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
        //X-Requested-With: XMLHttpRequest
        httpGet.setHeader("Host","sso.toutiao.com");
        httpGet.setHeader("Referer","https://sso.toutiao.com/");
        httpGet.setHeader("X-Requested-With","XMLHttpRequest");
    }

    private void setToutiaoHeader(HttpGet httpGet) {
        httpGet.setHeader("user-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        httpGet.setHeader("Host","sso.toutiao.com");
        //httpGet.setHeader("Referer","https://sso.toutiao.com/");
        httpGet.setHeader("Upgrade-Insecure-Requests","1");
        //Host: sso.toutiao.com
        //Upgrade-Insecure-Requests: 1
    }


    private void setLoginHeader(HttpPost httpPost) {
        //Host: sso.toutiao.com
        //Origin: https://sso.toutiao.com
        //Referer: https://sso.toutiao.com/
        //User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
        //X-CSRFToken: undefined
        //X-Requested-With: XMLHttpRequest
        httpPost.setHeader("Host","sso.toutiao.com");
        httpPost.setHeader("Origin","https://sso.toutiao.com/");
        httpPost.setHeader("Referer","https://sso.toutiao.com/");
        httpPost.setHeader("user-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        httpPost.setHeader("X-CSRFToken","undefined");
        httpPost.setHeader("X-Requested-With","XMLHttpRequest");
    }

    @Override
    public void loginOut(Long userid) {

    }
}
