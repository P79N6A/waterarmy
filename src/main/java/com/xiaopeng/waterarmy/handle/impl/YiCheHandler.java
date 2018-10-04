package com.xiaopeng.waterarmy.handle.impl;

import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.handle.PlatformHandler;
import com.xiaopeng.waterarmy.handle.Util.FetchParamUtil;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class YiCheHandler extends PlatformHandler {

    private static Logger logger = LoggerFactory.getLogger(TaiPingYangHandler.class);

    private static final String TOPIC = "topic";

    private static final String ForumApp = "forumApp";

    @Autowired
    private YiCheLoginHandler yiCheLoginHandler;

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
        Result<LoginResultDTO> resultDTOResult = yiCheLoginHandler.login(requestContext.getUserId());
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
            }
            HandlerResultDTO handlerResultDTO = createHandlerResultDTO(requestContext, content);
            return new Result<>(handlerResultDTO);
        } catch (Exception e) {
            logger.error("[TaiPingYangHandler.comment] error!", e);
        }
        return new Result<>(ResultCodeEnum.HANDLE_FAILED);
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
        /**
         * 自动评论的流程
         * 1.获取需要评论的帖子的url
         * 2.爬取该url，通过content获取相应参数
         * 3.组装页面参数及动态参数
         */


        //易车网评论需要的参数
        /**
         * attachments: []
         * title:
         * message: 哈哈，今天去看很赞
         * fid: 0
         * tid: 15742669
         * fgid: 0
         * forumApp: xiaopengqicheg3
         * type: 1
         * attachdesc:
         * txtCheckCode:
         * parentid:
         * floor:
         */

        String topic = null;
        String forumApp = null;
        if (requestContext.getRequestParam() != null) {
            Object object = requestContext.getRequestParam().get(TOPIC);
            if (object != null) {
                topic = (String) object;
            }

            Object object1 = requestContext.getRequestParam().get(ForumApp);
            if (object1 != null) {
                forumApp = (String) object1;
            }
        }

        if (topic == null) { //http://baa.bitauto.com/xiaopengqicheg3/thread-15742669.html
            topic = getTopic(requestContext.getPrefixUrl());
        }

        if (forumApp == null) { //http://baa.bitauto.com/xiaopengqicheg3/thread-15742669.html
            forumApp = getForumApp(requestContext.getPrefixUrl());
        }


        HttpPost httpPost = new HttpPost();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("message", requestContext.getContent().getText()));
        nameValuePairs.add(new BasicNameValuePair("tid", topic));
        nameValuePairs.add(new BasicNameValuePair("type", "1"));
        nameValuePairs.add(new BasicNameValuePair("fgid", "0"));
        nameValuePairs.add(new BasicNameValuePair("fid", "0"));
        nameValuePairs.add(new BasicNameValuePair("forumApp", forumApp));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (Exception e) {
            logger.error("[TaiPingYangHandler.createCommentPost]createCommentPost  UrlEncodedFormEntity error! nameValuePairs" + nameValuePairs);
            return null;
        }
        return httpPost;
    }

    private HandlerResultDTO createHandlerResultDTO(RequestContext requestContext, String content) {
        HandlerResultDTO handlerResultDTO = new HandlerResultDTO();
        handlerResultDTO.setHandleType(requestContext.getHandleType());
        handlerResultDTO.setDetailResult(content);
        handlerResultDTO.setPlatform(requestContext.getPlatform());
        handlerResultDTO.setUserId(requestContext.getUserId());
        handlerResultDTO.setUserLoginId(requestContext.getUserLoginId());
        return handlerResultDTO;
    }


    private String getTopic(String url) {
        String pattern = "(\\d+)";
        return FetchParamUtil.getMatherStr(url,pattern);
    }

    private String getForumApp(String url) {
        String pattern = "(\\/.*\\/)";
        return FetchParamUtil.getMatherStr(url,pattern);
    }


}
