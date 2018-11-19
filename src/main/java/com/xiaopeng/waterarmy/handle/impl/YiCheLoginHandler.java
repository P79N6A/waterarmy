package com.xiaopeng.waterarmy.handle.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.constants.RequestConsts;
import com.xiaopeng.waterarmy.common.enums.FeiFeiTypeEnum;
import com.xiaopeng.waterarmy.common.enums.HttpResultCode;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.handle.LoginHandler;
import com.xiaopeng.waterarmy.handle.LoginResultPool;
import com.xiaopeng.waterarmy.handle.Util.HttpFactory;
import com.xiaopeng.waterarmy.handle.Util.TranslateCodeUtil;
import com.xiaopeng.waterarmy.handle.Util.Util;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.mapper.AccountMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class YiCheLoginHandler implements LoginHandler {

    private static Logger logger = LoggerFactory.getLogger(TaiPingYangLoginHandler.class);

    public static final String refer = "http://i.bitauto.com/AuthenService/Frame/login.aspx?ra=0.5758006840153429&regtype=complex";

    public static final String UserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";

    public static final String Host = "i.bitauto.com";

    public static final String Origin = "http://i.yiche.com";

    public static final String XRequestedWith = "XMLHttpRequest";

    public static final String ContentType = "application/x-www-form-urlencoded; charset=UTF-8";

    private static final String loginUrl = "http://i.bitauto.com/ajax/Authenservice/login.ashx";

    private static final String returnUrl = "http://i.yiche.com/";

    private static final int MAX_RETRY_COUNT = 15;


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
        return this.login(account);
    }


    @Override
    public void loginOut(Long userid) {
        loginResultPool.removeLoginResult(String.valueOf(userid));
    }

    @Override
    public Result<LoginResultDTO> login(Account account) {
        if (account == null) {
            logger.error("[YiCheLoginHandler]login error; account is null");
            return new Result<>(ResultCodeEnum.INVALID_PARAM.getIndex(), ResultCodeEnum.INVALID_PARAM.getDesc());
        }

        String userName = account.getUserName();
        String passWord = account.getPassword();
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(passWord)) {
            logger.error("[YiCheLoginHandler]login error; param is invalid username is" + userName + "password is" + passWord);
            return new Result<>(ResultCodeEnum.INVALID_PARAM.getIndex(), ResultCodeEnum.INVALID_PARAM.getDesc());
        }
        LoginResultDTO loginResult = loginResultPool.getLoginResult(String.valueOf(account.getId()));
        if (loginResult != null) {
            return new Result<>(loginResult);
        }


        //易车登陆验证码识别比较费劲，一次登陆设置上限十次验证码校验
        int retry = MAX_RETRY_COUNT;
        while (retry > 0) {

            // 创建cookie store的本地实例
            CookieStore cookieStore = new BasicCookieStore();
            CloseableHttpClient httpClient = httpFactory.getHttpClientWithCookies(cookieStore);
            String guid = generateGuid();
            String fetchCodeUrl = generateCodeUrl(guid);
            //String code = TranslateCodeUtil.getInstance().convert(fetchCodeUrl);
            Util.HttpResp resp = TranslateCodeUtil.getInstance().convertByFeiFei(fetchCodeUrl, FeiFeiTypeEnum.FOUR_NUMBERS.getName());
            String code = resp.pred_resl;
            HttpPost httpPost = new HttpPost(loginUrl);
            setHeader(httpPost);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("txt_LoginName", userName));
            nameValuePairs.add(new BasicNameValuePair("txt_Password", passWord));
            nameValuePairs.add(new BasicNameValuePair("txt_Code", code));
            nameValuePairs.add(new BasicNameValuePair("cbx_keepState", "true"));
            nameValuePairs.add(new BasicNameValuePair("guid", guid));
            nameValuePairs.add(new BasicNameValuePair("Gamut", "true"));
            nameValuePairs.add(new BasicNameValuePair("returnurl", returnUrl));


            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                CloseableHttpResponse response = httpClient.execute(httpPost);
               /* if (response.getStatusLine().getStatusCode() != HttpResultCode.RESULT_OK.getResultCode()) {
                    logger.error("[YiCheLoginHandler.login] UrlEncodedFormEntity login failed! account is " + account);
                    return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
                }*/
                HttpEntity entity = response.getEntity();
                String content = EntityUtils.toString(entity, "utf-8");
                JSONObject jsonObject = JSONObject.parseObject(content);
                JSONObject itemsObject = (JSONObject) jsonObject.get("items");
                if (itemsObject != null && !content.contains("false")) {
                    //不包含false 说明已经成功了

                    //有时候不会返回，这样就没有种cookie，强制请求
                    JSONObject jsonObject1 = JSONObject.parseObject(RequestConsts.YICHELOGINPARAM);
                    JSONArray jsonArray1 = jsonObject1.getJSONArray("hideImg");

                    if (jsonArray1 != null) {
                        for (int i = 0; i < jsonArray1.size(); i++) {
                            String url = (String) jsonArray1.get(i);
                            long index = 1538475697342L + i;
                            url = "http:" + url + "&_=" + index;
                            HttpGet httpGet = new HttpGet(url);
                            CloseableHttpResponse response1 = httpClient.execute(httpGet);
                        }
                    }
                    LoginResultDTO loginResultDTO = new LoginResultDTO();
                    loginResultDTO.setHttpClient(httpClient);
                    for (Cookie c : cookieStore.getCookies()) {
                        if ("userid".equals(c.getName())) {
                            loginResultDTO.setOutUserId(c.getValue());
                        }
                        if ("username".equals(c.getName())) {
                            loginResultDTO.setOutUserName(c.getValue());
                        }
                    }
                    loginResultPool.putToLoginResultMap(String.valueOf(account.getId()), loginResultDTO);
                    return new Result<>(loginResultDTO);
                }
            } catch (Exception e) {
                logger.error("[YiCheLoginHandler.login] UrlEncodedFormEntity error! account is " + account, e);
            }
            try {
                Thread.sleep(300);
            } catch (Exception e) {

            }
            retry--;
        }
        return new Result<>(ResultCodeEnum.LOGIN_FAILED);
    }

    private void setHeader(HttpPost httpPost) {
        httpPost.setHeader("Origin", Origin);
        httpPost.setHeader("User-Agent", UserAgent);
        httpPost.setHeader("Referer", refer);
        httpPost.setHeader("Host", Host);
        httpPost.setHeader("X-Requested-With", XRequestedWith);
        httpPost.setHeader("Content-Type", ContentType);
    }

    private static String generateGuid() {
        StringBuilder prefix = new StringBuilder();
        StringBuilder suffix = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 7; i++) {
            prefix.append(random.nextInt(9));
        }
        for (int i = 0; i < 7; i++) {
            suffix.append(random.nextInt(9));
        }
        return "9" + prefix.toString() + "-5faf-8be7-b016-2a6cb" + suffix.toString();
    }

    private String generateCodeUrl(String guid) {
        String url = "http://i.bitauto.com/authenservice/common/CheckCode.aspx?guid=";
        return url + guid;
    }

}
