package com.xiaopeng.waterarmy;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * * 功能描述：
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="wenlong.cwl@alibaba-inc.com">成文龙</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/22
 */
public class Autohome {
    // Don't change the following URL
    private static String AutohomeRenLoginURL = "http://account.autohome.com.cn/Login/ValidIndex";

    // The HttpClient is used in one session
    HttpResponse response;
    private DefaultHttpClient httpclient = new DefaultHttpClient();

    private static String encode(String str) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("md5");
        return Hex.encodeHexString(digest.digest(str.getBytes()));

    }

    private boolean login() {
        String userName = "十七岁的雨季1";
        String password = null;//new MD5().GetMD5Code("a888888");
        try {
            password = encode("a888888");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        HttpPost httpost = new HttpPost(AutohomeRenLoginURL);
        // All the parameters post to the web site
        //建立一个NameValuePair数组，用于存储欲传送的参数
        List nvps = new ArrayList();
        nvps.add(new BasicNameValuePair("domain", "autohome.com.cn"));
        nvps.add(new BasicNameValuePair("isauto", "true"));
        nvps.add(new BasicNameValuePair("method", "post"));
        nvps.add(new BasicNameValuePair("name", userName));
        nvps.add(new BasicNameValuePair("pwd", password));
        try {
            httpost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));//HTTP.UTF_8
            response = httpclient.execute(httpost);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            httpost.abort();
        }
        return true;
    }

    private String getText(String redirectLocation) {
        HttpGet httpget = new HttpGet(redirectLocation);
        // Create a response handler
        ResponseHandler responseHandler = new BasicResponseHandler();
        String responseBody = "";

        //CloseableHttpResponse response;
        HttpResponse response;
        try {
            //获取html文件
            response = httpclient.execute(httpget);//, responseHandler);
            System.out.println("status:" + response.getStatusLine().getStatusCode());
            responseBody = EntityUtils.toString(response.getEntity(), "utf8");
            //response.getEntity().get
            //response =
             //responseBody =
        } catch (Exception e) {
            e.printStackTrace();
            responseBody = null;
        } finally {
            httpget.abort();
            httpclient.getConnectionManager().shutdown();
        }

        return responseBody;
    }

    public void printText(String redirectLocation) {
        if (login()) {
            if (redirectLocation != null) {
                System.out.println(getText(redirectLocation));
            }
        }
    }

    public void parser(String CrawlerUrl) {
        Autohome autohome = new Autohome();
        autohome.printText(CrawlerUrl);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        Autohome AutoHome = new Autohome();
        String CrawlerUrl = "http://i.autohome.com.cn/822110/info";
        AutoHome.parser(CrawlerUrl);
    }
}
