package com.xiaopeng.waterarmy.handle.impl;

import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.handle.PlatformHandler;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.param.SaveContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TaiPingYangHandler extends PlatformHandler {

    private static Logger logger = LoggerFactory.getLogger(TaiPingYangHandler.class);

    private static final String TOPIC = "topic";

    @Autowired
    TaiPingYangLoginHandler taiPingYangLoginHandler;


    @Override
    public Result save(SaveContext saveContext) {
        return null;
    }

    @Override
    public Result<HandlerResultDTO> publish(RequestContext requestContext) {


        return null;
    }

    @Override
    public Result<HandlerResultDTO> comment(RequestContext requestContext) {
        Result<LoginResultDTO> resultDTOResult = taiPingYangLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[TaiPingYangHandler.comment] requestContext"+ requestContext);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(),ResultCodeEnum.LOGIN_FAILED.getDesc());
        }
        try {
            LoginResultDTO loginResultDTO =  resultDTOResult.getData();
            CloseableHttpClient httpClient = loginResultDTO.getHttpClient();
            HttpPost httpPost = createCommentPost(requestContext);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = null;
            if (entity!=null) {
                content = EntityUtils.toString(entity, "utf-8");
            }
            HandlerResultDTO handlerResultDTO = createHandlerResultDTO(requestContext,content);
            return new Result<>(handlerResultDTO);
        }catch (Exception e) {
            logger.error("[TaiPingYangHandler.comment] error!",e);
        }
        return  new Result<>(ResultCodeEnum.HANDLE_FAILED);
    }

    @Override
    public Result<HandlerResultDTO> read(RequestContext requestContext) {
        return null;
    }

    @Override
    public Result<HandlerResultDTO> praise(RequestContext requestContext) {
        return null;
    }

    private HttpPost createCommentPost(RequestContext requestContext) {
        //评论
       /* HttpPost httpPost1 = new HttpPost("https://bbs.pcauto.com.cn/action/post/create.ajax");
        List<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();
        nameValuePairs1.add(new BasicNameValuePair("wysiwyg", "1"));
        nameValuePairs1.add(new BasicNameValuePair("fid", "17965"));
        nameValuePairs1.add(new BasicNameValuePair("topicTitleMaxLength", "40"));
        nameValuePairs1.add(new BasicNameValuePair("topicContentMinLength", "1"));
        nameValuePairs1.add(new BasicNameValuePair("topicContentMaxLength", "500000"));
        nameValuePairs1.add(new BasicNameValuePair("uploadKeepSource", "false"));
        nameValuePairs1.add(new BasicNameValuePair("uploadMaxNumPerTime", "9999"));
        nameValuePairs1.add(new BasicNameValuePair("checkCategory", "0"));
        nameValuePairs1.add(new BasicNameValuePair("tid", "17338209"));
        nameValuePairs1.add(new BasicNameValuePair("category", "综合"));
        nameValuePairs1.add(new BasicNameValuePair("message"
                , "[size=4]" + comment+ "[/size]"));//买车一个月，用起来真心不错，比想象的好很多
        nameValuePairs1.add(new BasicNameValuePair("upload2Album", "2982656"));
        nameValuePairs1.add(new BasicNameValuePair("albumId", "2982656"));
        nameValuePairs1.add(new BasicNameValuePair("sendMsg", "true"));
        httpPost1.setEntity(new UrlEncodedFormEntity(nameValuePairs1, "UTF-8"));
        CloseableHttpResponse response1 = httpClient.execute(httpPost1);
        HttpEntity entity1 = response1.getEntity();
        String content1 = EntityUtils.toString(entity1, "utf-8");
        System.out.println(content1);

        httpClient.close();*/


        /**
         * 自动评论的流程
         * 1.获取需要评论的帖子的url
         * 2.爬取该url，通过content获取相应参数
         * 3.组装页面参数及动态参数
         */


        //太平洋评论需要的参数
        /**
         * tid: 16868614
         * fid: 24155
         * message: 今天取看过 不错
         * needCaptcha: false
         * captcha:
         * sendMsg: true
         * minContentLength: 1
         * maxContentLength: 500000
         */

        String topic = null;
        if (requestContext.getRequestParam() != null) {
            Object object = requestContext.getRequestParam().get(TOPIC);
            if (object != null) {
                topic = (String) object;
            }
        }
        if (topic == null) { //https://bbs.pcauto.com.cn/topic-16868614.html
            //太平洋的可以直接从url中截取
            String pattern = "(\\d+)";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(requestContext.getPrefixUrl());
            if (m.find()) {
                topic = m.group(0);
            }
        }

        HttpPost httpPost = new HttpPost();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("message", requestContext.getContent().getText()));
        nameValuePairs.add(new BasicNameValuePair("fid", this.getFid(requestContext.getPrefixUrl())));
        nameValuePairs.add(new BasicNameValuePair("tid", topic));
        nameValuePairs.add(new BasicNameValuePair("sendMsg", "true"));
        nameValuePairs.add(new BasicNameValuePair("minContentLength", "1"));
        nameValuePairs.add(new BasicNameValuePair("maxContentLength", "500000"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        }catch (Exception e) {
            logger.error("[TaiPingYangHandler.createCommentPost]createCommentPost  UrlEncodedFormEntity error! nameValuePairs" + nameValuePairs);
            return null;
        }
        return httpPost;

       /* HttpPost httpPost1 = new HttpPost(requestContext.getPrefixUrl());
        List<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();
        nameValuePairs1.add(new BasicNameValuePair("wysiwyg", "1"));
        nameValuePairs1.add(new BasicNameValuePair("fid", "17965"));
        nameValuePairs1.add(new BasicNameValuePair("topicTitleMaxLength", "40"));
        nameValuePairs1.add(new BasicNameValuePair("topicContentMinLength", "1"));
        nameValuePairs1.add(new BasicNameValuePair("topicContentMaxLength", "500000"));
        nameValuePairs1.add(new BasicNameValuePair("uploadKeepSource", "false"));
        nameValuePairs1.add(new BasicNameValuePair("uploadMaxNumPerTime", "9999"));
        nameValuePairs1.add(new BasicNameValuePair("checkCategory", "0"));
        nameValuePairs1.add(new BasicNameValuePair("tid", "17338209"));
        nameValuePairs1.add(new BasicNameValuePair("category", "综合"));
        nameValuePairs1.add(new BasicNameValuePair("message"
                , "[size=4]" + comment+ "[/size]"));//买车一个月，用起来真心不错，比想象的好很多
        nameValuePairs1.add(new BasicNameValuePair("upload2Album", "2982656"));
        nameValuePairs1.add(new BasicNameValuePair("albumId", "2982656"));
        nameValuePairs1.add(new BasicNameValuePair("sendMsg", "true"));
        httpPost1.setEntity(new UrlEncodedFormEntity(nameValuePairs1, "UTF-8"));
        CloseableHttpResponse response1 = httpClient.execute(httpPost1);
        HttpEntity entity1 = response1.getEntity();
        String content1 = EntityUtils.toString(entity1, "utf-8");
        System.out.println(content1);

        httpClient.close();*/

        //return null;
    }

    private String getFid(String url) {
        try {
            Document doc = Jsoup.connect(url).timeout(2000).get();
            Element content = doc.getElementById("fixBox");
            String tid = content.attr("data-src");
            return tid;
        } catch (Exception e) {

        }
        return null;
    }

    private HandlerResultDTO createHandlerResultDTO(RequestContext requestContext,String content){
        HandlerResultDTO handlerResultDTO = new HandlerResultDTO();
        handlerResultDTO.setHandleType(requestContext.getHandleType());
        handlerResultDTO.setDetailResult(content);
        handlerResultDTO.setPlatform(requestContext.getPlatform());
        handlerResultDTO.setUserId(requestContext.getUserId());
        handlerResultDTO.setUserLoginId(requestContext.getUserLoginId());
        return handlerResultDTO;
    }
}


