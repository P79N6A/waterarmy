package com.xiaopeng.waterarmy.handle.impl;

import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.enums.HttpResultCode;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.handle.LoginHandler;
import com.xiaopeng.waterarmy.handle.LoginResultPool;
import com.xiaopeng.waterarmy.handle.Util.HttpFactory;
import com.xiaopeng.waterarmy.handle.Util.TranslateCodeUtil;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.mapper.AccountMapper;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class YiCheLoginHandler  implements LoginHandler {

    private static Logger logger = LoggerFactory.getLogger(TaiPingYangLoginHandler.class);

    public static final String refer = "http://i.bitauto.com/AuthenService/Frame/login.aspx";

    public static final String UserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";

    public static final String Host = "i.bitauto.com";

    public static final String Origin = "http://i.yiche.com";

    public static final String XRequestedWith ="X-Requested-With";

    public static final String ContentType = "application/x-www-form-urlencoded; charset=UTF-8";

    private static final String loginUrl = "http://i.bitauto.com/ajax/Authenservice/login.ashx";

    private static final String returnUrl = "http://i.yiche.com/";



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
        LoginResultDTO loginResult = loginResultPool.getLoginResult(account.getUserName());
        if (loginResult != null) {
            return new Result<>(loginResult);
        }
        CloseableHttpClient httpClient = httpFactory.getHttpClient();

        String guid = generateGuid();
        String fetchCodeUrl = generateCodeUrl(guid);
        String code = TranslateCodeUtil.getInstance().convert(fetchCodeUrl);

        HttpPost httpPost = new HttpPost(loginUrl);
        setHeader(httpPost);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("txt_LoginName", userName));
        nameValuePairs.add(new BasicNameValuePair("txt_Password", passWord));
        nameValuePairs.add(new BasicNameValuePair("txt_Code", code));
        nameValuePairs.add(new BasicNameValuePair("guid",guid));
        nameValuePairs.add(new BasicNameValuePair("cbx_keepState", "true"));
        nameValuePairs.add(new BasicNameValuePair("returnurl", returnUrl));
        nameValuePairs.add(new BasicNameValuePair("Gamut", "true"));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != HttpResultCode.RESULT_OK.getResultCode()) {
                logger.error("[YiCheLoginHandler.login] UrlEncodedFormEntity login failed! account is " + account);
                return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
            }
            /* HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            System.out.println(content);
            System.out.println(httpPost.getURI());
            System.out.println(response);*/
        } catch (Exception e) {
            logger.error("[YiCheLoginHandler.login] UrlEncodedFormEntity error! account is " + account, e);
        }
        LoginResultDTO loginResultDTO = new LoginResultDTO();
        loginResultDTO.setHttpClient(httpClient);
        loginResultPool.putToLoginResultMap(account.getUserName(), loginResultDTO);
        return new Result<>(loginResultDTO);
    }

    private void setHeader(HttpPost httpPost) {
        httpPost.setHeader("Origin",Origin);
        httpPost.setHeader("User-Agent",UserAgent);
        httpPost.setHeader("Referer",refer);
        httpPost.setHeader("Host",Host);
        httpPost.setHeader("X-Requested-With",XRequestedWith);
        httpPost.setHeader("Content-Type",ContentType);
    }

    private static String generateGuid() {
        StringBuilder prefix = new StringBuilder();
        StringBuilder suffix = new StringBuilder();
        Random random = new Random();
        for (int i=0;i<7;i++) {
            prefix.append(random.nextInt(9));
        }
        for (int i=0;i<7;i++) {
            suffix.append(random.nextInt(9));
        }
        return "9"+prefix.toString()+"-5faf-8be7-b016-2a6cb"+suffix.toString();
    }

    private String generateCodeUrl(String guid) {
        String url = "http://i.bitauto.com/authenservice/common/CheckCode.aspx?guid=";
        return url+guid;
    }

}
