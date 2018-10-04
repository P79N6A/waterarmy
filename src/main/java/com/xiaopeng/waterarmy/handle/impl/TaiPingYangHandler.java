package com.xiaopeng.waterarmy.handle.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.constants.ResultConstants;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.handle.PlatformHandler;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.param.SaveContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import com.xiaopeng.waterarmy.handle.result.TaiPingYangCommentResultDTO;
import com.xiaopeng.waterarmy.model.dao.CommentInfo;
import org.apache.commons.lang3.StringUtils;
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

    private static final String TARGET_COMMENT_URL = "https://bbs.pcauto.com.cn/action/post/create.ajax";

    @Autowired
    TaiPingYangLoginHandler taiPingYangLoginHandler;


    @Override
    public Result<HandlerResultDTO> publish(RequestContext requestContext) {


        return null;
    }

    @Override
    public Result<HandlerResultDTO> comment(RequestContext requestContext) {
        Result<LoginResultDTO> resultDTOResult = taiPingYangLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[TaiPingYangHandler.comment] requestContext" + requestContext);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }
        try {
            LoginResultDTO loginResultDTO = resultDTOResult.getData();
            CloseableHttpClient httpClient = loginResultDTO.getHttpClient();
            HttpPost httpPost = createCommentPost(requestContext);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = null;
            if (entity != null) {
                content = EntityUtils.toString(entity, "utf-8");
                TaiPingYangCommentResultDTO resultDTO = JSONObject.parseObject(content, TaiPingYangCommentResultDTO.class);
                if (resultDTO.getStatus().equals("0")) {
                    //评论成功
                    HandlerResultDTO handlerResultDTO = createHandlerResultDTO(requestContext, content);
                    CommentInfo commentInfo = createCommentInfo(requestContext, content);

                    save(new SaveContext(commentInfo));

                    return new Result(handlerResultDTO);
                }
            }
            //处理失败的回复，把context记录下来，可以决定是否重新扫描,并且记录失败原因,这个在业务层处理，决定是否要记录未处理成功的数据
            return new Result<>(ResultCodeEnum.HANDLE_FAILED);
        } catch (Exception e) {
            logger.error("[TaiPingYangHandler.comment] error!", e);
            //处理失败的回复，把context记录下来，可以决定是否重新扫描,并且记录失败原因
            return new Result<>(ResultCodeEnum.HANDLE_FAILED);
        }

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

        String targetUrl = requestContext.getTargetUrl();
        if (StringUtils.isBlank(targetUrl)) {
            targetUrl = TARGET_COMMENT_URL;
        }
        HttpPost httpPost = new HttpPost(targetUrl);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("message", requestContext.getContent().getText()));
        nameValuePairs.add(new BasicNameValuePair("fid", this.getFid(requestContext.getPrefixUrl())));
        nameValuePairs.add(new BasicNameValuePair("tid", topic));
        nameValuePairs.add(new BasicNameValuePair("sendMsg", "true"));
        nameValuePairs.add(new BasicNameValuePair("minContentLength", "1"));
        nameValuePairs.add(new BasicNameValuePair("maxContentLength", "500000"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (Exception e) {
            logger.error("[TaiPingYangHandler.createCommentPost]createCommentPost  UrlEncodedFormEntity error! nameValuePairs" + nameValuePairs);
            return null;
        }
        return httpPost;
    }

    private String getFid(String url) {

        int count = 3;
        while (count > 0) {
            try {
                Document doc = Jsoup.connect(url).timeout(2000).get();
                Element content = doc.getElementById("fixBox");
                String tid = content.attr("data-src");
                return tid;
            } catch (Exception e) {
                count--;
            }
        }
        return null;
    }

    private HandlerResultDTO createHandlerResultDTO(RequestContext requestContext, String content) {
        HandlerResultDTO handlerResultDTO = new HandlerResultDTO();
        handlerResultDTO.setHandleType(requestContext.getHandleType());
        handlerResultDTO.setDetailResult(content);
        handlerResultDTO.setPlatform(requestContext.getPlatform());
        handlerResultDTO.setUserId(requestContext.getUserId());
        handlerResultDTO.setUserLoginId(requestContext.getUserLoginId());
        handlerResultDTO.setTargetUrl(requestContext.getTargetUrl());
        return handlerResultDTO;
    }


    private CommentInfo createCommentInfo(RequestContext requestContext, String content) {
        CommentInfo commentInfo = new CommentInfo();
        commentInfo.setOutUserName(requestContext.getUserLoginId());
        commentInfo.setDetailResult(content);
        commentInfo.setPlatform(requestContext.getPlatform().getName());
        commentInfo.setStatus(ResultConstants.STATUS_ENABLE);
        commentInfo.setUserId(requestContext.getUserId());
        commentInfo.setTargetUrl(requestContext.getTargetUrl());
        return commentInfo;
    }
}


