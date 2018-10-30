package com.xiaopeng.waterarmy.handle.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.handle.LoginHandler;
import com.xiaopeng.waterarmy.handle.LoginResultPool;
import com.xiaopeng.waterarmy.handle.Util.HttpFactory;
import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.mapper.AccountMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
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
import java.util.List;

@Component
public class QiCheZhiJiaLoginHandler implements LoginHandler {
    private static Logger logger = LoggerFactory.getLogger(QiCheZhiJiaLoginHandler.class);

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
            logger.error("[QiCheZhiJiaLoginHandler]login error; account is null");
            return new Result<>(ResultCodeEnum.INVALID_PARAM.getIndex(), ResultCodeEnum.INVALID_PARAM.getDesc());
        }
        BasicCookieStore cookieStore;
        String userName = account.getUserName();
        String passWord = account.getPassword();
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(passWord)) {
            logger.error("[QiCheZhiJiaLoginHandler]login error; param is invalid username is" + userName + "password is" + passWord);
            return new Result<>(ResultCodeEnum.INVALID_PARAM.getIndex(), ResultCodeEnum.INVALID_PARAM.getDesc());
        }
        LoginResultDTO loginResult = loginResultPool.getLoginResult(String.valueOf(account.getId()));
        if (loginResult != null) {
            return new Result<>(loginResult);
        }


        //重试1次,汽车之家风控拦截，不能多次重试
        int retry = 1;
        while (retry>0) {
            retry--;
            cookieStore = new BasicCookieStore();
            CloseableHttpClient httpClient = httpFactory.getHttpClientWithCookies(cookieStore);
            Result<LoginResultDTO> result = validateByJiyan(httpClient, account);
            result.getData().setCookieStore(cookieStore);
            if (result.getSuccess()) {
                return result;
            }
        }
        return new Result(ResultCodeEnum.LOGIN_FAILED);
    }

    public Result<LoginResultDTO> validateByJiyan(CloseableHttpClient httpClient, Account account) {
        String gt = null;
        String challenge = null;
        try {
            //获取cookie
            //https://account.autohome.com.cn/
            String getCookie = "https://account.autohome.com.cn/";
            HttpGet cookieHttpGet = new HttpGet(getCookie);
            httpClient.execute(cookieHttpGet);


            Long ll = System.currentTimeMillis();
            String url = "https://account.autohome.com.cn/AccountApi/GetCaptcha?site=web&t=" + ll;

            //TODO 获取validate

            HttpGet httpGet = new HttpGet(url);
            setHeader(httpGet);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            JSONObject jsonObject = JSONObject.parseObject(content);
            gt = (String) jsonObject.get("gt");
            challenge = (String) jsonObject.get("challenge");


            //add validate
            //https://account.autohome.com.cn/ValidateCode/AddValidateCode
            String validateUrl = "https://account.autohome.com.cn/ValidateCode/AddValidateCode";
            HttpPost httpPost1 = new HttpPost(validateUrl);
            CloseableHttpResponse response1 = httpClient.execute(httpPost1);
            HttpEntity entity1 = response1.getEntity();
            String content1 = EntityUtils.toString(entity1, "utf-8");
            System.out.println(content1);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("qichezhijialogin", e);
        }

        if (gt == null || challenge == null) {
            return new Result(ResultCodeEnum.LOGIN_FAILED);
        }


        //http://jiyan.c2567.com/index.php/user2/index.html

        String jiyan_url = "http://jiyanapi.c2567.com/shibie?gt=" + gt + "&challenge=" + challenge + "&referer=https://account.autohome.com.cn/&user=watermy&pass=qwertyui123&return=json&model=3&format=utf8";
        //极验验证识别
        try {
            CloseableHttpResponse response = httpClient.execute(new HttpGet(jiyan_url));

            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            JSONObject jsonObject = JSONObject.parseObject(content);

            String jiyan_validate = (String) jsonObject.get("validate");
            String jiyan_challenge = (String) jsonObject.get("challenge");
            String seccode = jiyan_validate + "|jordan";
            boolean login = setParam(httpClient, jiyan_challenge, seccode, jiyan_validate, account.getUserName(), account.getPassword());
            if (login) {
                //登陆成功
                LoginResultDTO loginResultDTO = new LoginResultDTO();
                loginResultDTO.setHttpClient(httpClient);
                loginResultPool.putToLoginResultMap(String.valueOf(account.getId()), loginResultDTO);
                return new Result<>(loginResultDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result<>(ResultCodeEnum.LOGIN_FAILED);
    }

    public boolean setParam(CloseableHttpClient httpClient, String challenge, String seccode, String validate, String username, String password) {
        try {
//评论:
            HttpPost httpPost1 = new HttpPost("https://account.autohome.com.cn/Login/ValidIndex");
            setHeader(httpPost1);
            List<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();
            // nameValuePairs1.add(new BasicNameValuePair("name", "13438369217"));
            nameValuePairs1.add(new BasicNameValuePair("name", username));
            //nameValuePairs1.add(new BasicNameValuePair("pwd", "3f9a81af6a76fe603beac02003056c09"));
            nameValuePairs1.add(new BasicNameValuePair("pwd", DigestUtils.md5Hex(password)));
            nameValuePairs1.add(new BasicNameValuePair("isauto", "true"));
            nameValuePairs1.add(new BasicNameValuePair("type", "json"));
            nameValuePairs1.add(new BasicNameValuePair("fPosition", "0"));
            nameValuePairs1.add(new BasicNameValuePair("sPosition", "0"));
            nameValuePairs1.add(new BasicNameValuePair("platform", "1"));
            nameValuePairs1.add(new BasicNameValuePair("popWindow", "0"));
            nameValuePairs1.add(new BasicNameValuePair("validcode", ""));
            nameValuePairs1.add(new BasicNameValuePair("backurl", ""));
            nameValuePairs1.add(new BasicNameValuePair("url", ""));
            nameValuePairs1.add(new BasicNameValuePair("geetest_challenge", challenge));
            nameValuePairs1.add(new BasicNameValuePair("geetest_seccode", seccode));
            nameValuePairs1.add(new BasicNameValuePair("geetest_validate", validate));
            httpPost1.setEntity(new UrlEncodedFormEntity(nameValuePairs1, "UTF-8"));
            CloseableHttpResponse response1 = httpClient.execute(httpPost1);
            HttpEntity entity1 = response1.getEntity();
            String content1 = EntityUtils.toString(entity1, "utf-8");
            System.out.println(content1);
            //登陆成功,设置httpclient的cookie
            if (content1.contains("Msg.Success")) {
                JSONObject jsonObject = JSONObject.parseObject(content1);
                String loginUrl = (String) jsonObject.get("LoginUrl");
                String ssoAutohomeUrl = (String) jsonObject.get("ssoAutohomeUrl");
                String ssoChe168Url = (String) jsonObject.get("ssoChe168Url");
                String loginUrlJiaJiaBX = (String) jsonObject.get("loginUrlJiaJiaBX");

                HttpGet httpGet = new HttpGet("https:"+loginUrl);
                HttpGet httpGet3 = new HttpGet("https:"+loginUrlJiaJiaBX);
                HttpGet httpGet1 = new HttpGet(ssoAutohomeUrl);
                HttpGet httpGet2 = new HttpGet(ssoChe168Url);

                setSyncSSOHeader(httpGet1);
                setChe168Header(httpGet);
                setJiaJiaBaoxianHeader(httpGet3);
                setSSOChe168Header(httpGet2);


                httpClient.execute(httpGet1);
               /* try {
                    //httpClient.execute(httpGet);
                }catch (Exception e) {
                    e.printStackTrace();
                }*/

                try {
                    httpClient.execute(httpGet2);
                }catch (Exception e) {
                    e.printStackTrace();
                }

              /*  try {
                    //httpClient.execute(httpGet3);
                }catch (Exception e) {
                    e.printStackTrace();
                }*/

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setChe168Header(HttpGet httpGet) {
        //Accept: */*
        //Accept-Encoding: gzip, deflate, br
        //Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
        //Connection: keep-alive
        //Cookie: pcpopclub=8872a960692944d6a711f013e874b17104fadfac; clubUserShow=83550124|66|30|ao2yhn9t7|0|0|0||2018-10-26 08:52:00|0; autouserid=83550124; sessionuserid=83550124; sessionlogin=c1f63c343c05495ca687da4602b0d8db04fadfac
        //Host: account.che168.com
        //Referer: https://account.autohome.com.cn/
        //User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
        httpGet.setHeader("Accept", "*/*");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, br");
        httpGet.setHeader("Host", "account.che168.com");
        httpGet.setHeader("Referer", "https://account.autohome.com.cn/");
        httpGet.setHeader("User-Agent", "User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
    }

    public void setSSOChe168Header(HttpGet httpGet) {
        //Accept: */*
        //Accept-Encoding: gzip, deflate, br
        //Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
        //Connection: keep-alive
        //Cookie: __ah_uuid=1FAC53EE-E0FE-43B1-A5C1-BDCCB4AA3B1B; fvlid=1540471585776iwnlf7AfSm; sessionid=71B8270C-17BA-4009-9DDA-7E3A53138FE3%7C%7C2018-10-25+20%3A46%3A28.043%7C%7Cwww.baidu.com; ahpau=1; sessionuid=71B8270C-17BA-4009-9DDA-7E3A53138FE3%7C%7C2018-10-25+20%3A46%3A28.043%7C%7Cwww.baidu.com; __utmc=1; __utmz=1.1540513111.2.2.utmcsr=autohome.com.cn|utmccn=(referral)|utmcmd=referral|utmcct=/hangzhou/; autouserid=83550124; sessionuserid=83550124; autosso=b10d9f04dd5f4a32a2488f8836509c3d04fadfac; sessionlogin=37ffc5aef6d748f59225d32672e8b8cb04fadfac; ahpvno=19; sessionip=115.200.236.208; sessionvid=9D5FA86F-8915-4DE2-8AE2-DDDB0B838DB7; area=330108; __utma=1.173950538.1540471590.1540513111.1540530802.3; __utmb=1.0.10.1540530802; ref=www.baidu.com%7C0%7C0%7C0%7C2018-10-26+13%3A13%3A24.797%7C2018-10-25+20%3A46%3A28.043; ahrlid=1540530801367o54l3qfTE6-1540530844693; pcpopclub=fd1d09677eee4be4a0e1c16ebaad55f104fadfac; clubUserShow=83550124|66|30|ao2yhn9t7|0|0|0||2018-10-26 13:14:03|0
        //Host: sso.autohome.com.cn
        //Referer: https://account.autohome.com.cn/
        //User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
        httpGet.setHeader("Host", "sso.che168.com");
        httpGet.setHeader("Referer", "https://account.autohome.com.cn/");
        httpGet.setHeader("User-Agent", "User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
    }


    public  void setSyncSSOHeader(HttpGet httpGet) {
        //Accept: */*
        //Accept-Encoding: gzip, deflate, br
        //Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
        //Connection: keep-alive
        //Cookie: __ah_uuid=1FAC53EE-E0FE-43B1-A5C1-BDCCB4AA3B1B; fvlid=1540471585776iwnlf7AfSm; sessionid=71B8270C-17BA-4009-9DDA-7E3A53138FE3%7C%7C2018-10-25+20%3A46%3A28.043%7C%7Cwww.baidu.com; ahpau=1; sessionuid=71B8270C-17BA-4009-9DDA-7E3A53138FE3%7C%7C2018-10-25+20%3A46%3A28.043%7C%7Cwww.baidu.com; __utmc=1; __utmz=1.1540513111.2.2.utmcsr=autohome.com.cn|utmccn=(referral)|utmcmd=referral|utmcct=/hangzhou/; autouserid=83550124; sessionuserid=83550124; autosso=b10d9f04dd5f4a32a2488f8836509c3d04fadfac; sessionlogin=37ffc5aef6d748f59225d32672e8b8cb04fadfac; ahpvno=19; sessionip=115.200.236.208; sessionvid=9D5FA86F-8915-4DE2-8AE2-DDDB0B838DB7; area=330108; __utma=1.173950538.1540471590.1540513111.1540530802.3; __utmb=1.0.10.1540530802; ref=www.baidu.com%7C0%7C0%7C0%7C2018-10-26+13%3A13%3A24.797%7C2018-10-25+20%3A46%3A28.043; ahrlid=1540530801367o54l3qfTE6-1540530844693; pcpopclub=fd1d09677eee4be4a0e1c16ebaad55f104fadfac; clubUserShow=83550124|66|30|ao2yhn9t7|0|0|0||2018-10-26 13:14:03|0
        //Host: sso.autohome.com.cn
        //Referer: https://account.autohome.com.cn/
        //User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
        httpGet.setHeader("Host", "sso.autohome.com.cn");
        httpGet.setHeader("Referer", "https://account.autohome.com.cn/");
        httpGet.setHeader("User-Agent", "User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
    }

    private void setJiaJiaBaoxianHeader(HttpGet httpGet) {
        //Accept: */*
        //Accept-Encoding: gzip, deflate, br
        //Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
        //Connection: keep-alive
        //Cookie: autouserid=83550124; sessionuserid=83550124; pcpopclub=01102ca968e440c085d5a7726961299e04fadfac; clubUserShow=83550124|66|30|ao2yhn9t7|0|0|0||2018-10-26 08:52:02|0; sessionlogin=198786536fc84f51ad5fbca5b8b3820c04fadfac
        //Host: account.jiajiabaoxian.com.cn
        //Referer: https://account.autohome.com.cn/
        //User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
        httpGet.setHeader("Host", "account.jiajiabaoxian.com.cn");
        httpGet.setHeader("Referer", "https://account.autohome.com.cn/");
        httpGet.setHeader("User-Agent", "User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
    }

    public static void setHeader(HttpGet httpGet) {
        httpGet.setHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, br");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Host", "account.autohome.com.cn");
        httpGet.setHeader("Referer", "https://account.autohome.com.cn/");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
        httpGet.setHeader("X-Requested-With", "XMLHttpRequest");
    }

    public static void setHeader(HttpPost httpPost) {
        httpPost.setHeader("Accept", "*/*");
        httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
        httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpPost.setHeader("Connection", "keep-alive");
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.setHeader("Host", "account.autohome.com.cn");
        httpPost.setHeader("Origin", "https://account.autohome.com.cn");
        httpPost.setHeader("Referer", "https://account.autohome.com.cn/");
        httpPost.setHeader("User-Agent", "User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
    }

    @Override
    public Result<LoginResultDTO> login(Long userid, Boolean forceToLogin) {
        return null;
    }

    @Override
    public void loginOut(Long userid) {

    }
}
