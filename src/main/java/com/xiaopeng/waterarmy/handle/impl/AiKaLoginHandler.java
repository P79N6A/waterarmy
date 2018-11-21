package com.xiaopeng.waterarmy.handle.impl;

import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.enums.FeiFeiTypeEnum;
import com.xiaopeng.waterarmy.common.enums.HttpResultCode;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.handle.LoginHandler;
import com.xiaopeng.waterarmy.handle.LoginResultPool;
import com.xiaopeng.waterarmy.handle.Util.FetchParamUtil;
import com.xiaopeng.waterarmy.handle.Util.HttpFactory;
import com.xiaopeng.waterarmy.handle.Util.TranslateCodeUtil;
import com.xiaopeng.waterarmy.handle.Util.Util;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.dto.ProxyHttpConfig;
import com.xiaopeng.waterarmy.model.mapper.AccountMapper;
import org.apache.commons.codec.digest.DigestUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class AiKaLoginHandler implements LoginHandler {

    private static Logger logger = LoggerFactory.getLogger(AiKaLoginHandler.class);

    public static final String UserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";

    public static final String Host = "my.xcar.com.cn";

    public static final String Origin = "http://my.xcar.com.cn";

    //private static final String loginUrl = "http://reg.xcar.com.cn/ajax/ajax_dologin.php";
    private static final String loginUrl = "http://my.xcar.com.cn/ajax/ajax_dologin.php";

    private static final String XMLHttpRequest = "XMLHttpRequest";

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
    public Result<LoginResultDTO> login(RequestContext requestContext) {
        Long userid = requestContext.getUserId();
        if (userid == null || userid < 1L) {
            logger.error("[TaiPingYangLoginHandler]login error; userid is null");
            return new Result<>(ResultCodeEnum.INVALID_PARAM.getIndex(), ResultCodeEnum.INVALID_PARAM.getDesc());
        }
        Account account = accountMapper.getAccountById(Long.valueOf(userid));
        account.setProxyHttpConfig(requestContext.getProxyHttpConfig());
        return this.login(account);
    }

    /**
     * logintype: 2
     * username: 2222222222
     * userpwd: 777cac5d6f1387dec2965c69a9dbe9e9
     * u:
     * checkcode: 请输入验证码
     *
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
        LoginResultDTO loginResult = loginResultPool.getLoginResult(String.valueOf(account.getId()));
        if (loginResult != null) {
            return new Result<>(loginResult);
        }
        CloseableHttpClient httpClient = httpFactory.getHttpClient();


        int retry = 5;
        boolean needVerifyCode = false;
        boolean validateSuccess = false;

        while (retry > 0) {
            retry--;
            /**
             * username: 18394791951
             * userpwd: 2ec65cdb90010ba8920fa8649c73f926
             * logintype: 2
             * checkcode: 4688
             * uniquekey: a94737cfcde96d69cd01944f57d24e6e
             */
            try {
                HttpPost httpPost = new HttpPost(loginUrl);
                setHeader(httpPost);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username", userName));
                nameValuePairs.add(new BasicNameValuePair("userpwd", DigestUtils.md5Hex(passWord)));
                nameValuePairs.add(new BasicNameValuePair("logintype", "2"));
                Util.HttpResp resp = null;
                if (needVerifyCode) {
                    String time = String.valueOf(new Date().getTime());
                    String codeToken = getCodeToken(httpClient, time);
                    String codeUrl = getValidateCodeUrl(httpClient, codeToken, time);
                    resp = TranslateCodeUtil.getInstance().convertByFeiFei(codeUrl, FeiFeiTypeEnum.FOUR_NUMBERS.getName());
                    if (StringUtils.isNotBlank(resp.pred_resl)) {
                        validateSuccess = true;
                    } else {
                        continue;
                    }
                    nameValuePairs.add(new BasicNameValuePair("checkcode", resp.pred_resl));
                    nameValuePairs.add(new BasicNameValuePair("uniquekey", codeToken));
                } else {
                    nameValuePairs.add(new BasicNameValuePair("checkcode", "请输入验证码"));
                    nameValuePairs.add(new BasicNameValuePair("uniquekey", null));
                }


                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                CloseableHttpResponse response = httpClient.execute(httpPost);
               /* if (response.getStatusLine().getStatusCode() != HttpResultCode.RESULT_OK.getResultCode()) {
                    logger.error("[AiKaLoginHandler.login] UrlEncodedFormEntity login failed! account is " + account);
                    httpPost.completed();
                    httpClient.close();
                    return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
                }*/
                HttpEntity entity = response.getEntity();
                String content = EntityUtils.toString(entity, "utf-8");
                if (content.contains("\\u9a8c\\u8bc1\\u7801\\u9519\\u8bef") || content.contains("\\u8bf7\\u8f93\\u5165\\u9a8c\\u8bc1\\u7801")) {
                    if (validateSuccess) {
                        //结果告知成功，但是却未通过系统校验退款
                        TranslateCodeUtil.getInstance().refund(resp.req_id);
                    }
                    needVerifyCode = true;
                    validateSuccess = false;
                    continue;
                }
                if (content.contains("secques")) {
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

        }

        return new Result(ResultCodeEnum.LOGIN_FAILED);
    }

    private void setHeader(HttpPost httpPost) {
        httpPost.setHeader("User-Agent", UserAgent);
        httpPost.setHeader("Origin", Origin);
        httpPost.setHeader("Host", Host);
        httpPost.setHeader("X-Requested-With", XMLHttpRequest);
    }

    private void setHeader(HttpGet httpGet) {
        httpGet.setHeader("User-Agent", UserAgent);
        httpGet.setHeader("Origin", Origin);
        httpGet.setHeader("Host", Host);
    }


    private String getCodeToken(CloseableHttpClient httpClient, String time) {
        try {
            String tokenUrl = "http://my.xcar.com.cn/login_check_code.php?action=getu&t=";
            tokenUrl = tokenUrl + time;
            HttpGet httpGet = new HttpGet(tokenUrl);
            setHeader(httpGet);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content = null;
            if (entity != null) {
                content = EntityUtils.toString(entity, "utf-8");
                content = content.replaceAll("\"", "");
                return content;
            }
        } catch (Exception e) {
            logger.error("[AiKaLoginHandler.getCodeToken] error!", e);
        }
        return null;
    }


    private String getValidateCodeUrl(CloseableHttpClient httpClient, String code, String time) {
        if (StringUtils.isBlank(code)) {
            logger.error("[AiKaLoginHandler.getValidateCodeUrl] error!");
            return null;
        }
        String url = "http://my.xcar.com.cn/login_check_code.php?u=" + code + "&t=" + time;
        return url;
    }

    @Override
    public void loginOut(Long userid) {
        loginResultPool.removeLoginResult(String.valueOf(userid));
    }
}
