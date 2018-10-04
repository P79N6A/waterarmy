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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class DiYiDianDongHandler extends PlatformHandler {

    private static Logger logger = LoggerFactory.getLogger(DiYiDianDongHandler.class);

    @Autowired
    private DiYiDianDongLoginHandler diYiDianDongLoginHandler;

    @Override
    public Result save(SaveContext saveContext) {
        return null;
    }

    @Override
    public Result<HandlerResultDTO> publish(RequestContext requestContext) {
        return null;
    }

    /**
     *   $(function () {
     *                 var review1 = new ReviewSys();
     *                 review1.init({
     *                     targetId: 77669, // 文章ID
     *                     targetType: 10, // 文章类型
     *                     publisherId: 0, // 作者ID
     *                     userId: '', // 登录用户id, https://www.d1ev.com/user/176252
     *                     source: 2, // 来源 2pc ,3wap
     *                     domain: 'https://www.d1ev.com',
     *                     size: 20, // 分页条数
     *                     page: 1 // 当前页
     *                 });
     *             });
     * @param requestContext
     * @return
     */

    @Override
    public Result<HandlerResultDTO> comment(RequestContext requestContext) {

        Result<LoginResultDTO> resultDTOResult = diYiDianDongLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[DiYiDianDongHandler.comment] requestContext" + requestContext);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }
        try {
            LoginResultDTO loginResultDTO = resultDTOResult.getData();
            CloseableHttpClient httpClient = loginResultDTO.getHttpClient();
            HttpPost httpPost = createCommentPost(requestContext,loginResultDTO);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = null;
            if (entity != null) {
                content = EntityUtils.toString(entity, "utf-8");
            }
            HandlerResultDTO handlerResultDTO = createHandlerResultDTO(requestContext, content);
            return new Result<>(handlerResultDTO);
        } catch (Exception e) {
            logger.error("[DiYiDianDongHandler.comment] error!", e);
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


    private HandlerResultDTO createHandlerResultDTO(RequestContext requestContext, String content) {
        HandlerResultDTO handlerResultDTO = new HandlerResultDTO();
        handlerResultDTO.setHandleType(requestContext.getHandleType());
        handlerResultDTO.setDetailResult(content);
        handlerResultDTO.setPlatform(requestContext.getPlatform());
        handlerResultDTO.setUserId(requestContext.getUserId());
        handlerResultDTO.setUserLoginId(requestContext.getUserLoginId());
        return handlerResultDTO;
    }

    private HttpPost createCommentPost(RequestContext requestContext,LoginResultDTO loginResultDTO) {
        try {
            CloseableHttpClient closeableHttpClient = loginResultDTO.getHttpClient();
            HttpGet httpGet = new HttpGet(requestContext.getPrefixUrl());
            CloseableHttpResponse response = closeableHttpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String body = EntityUtils.toString(entity, "utf-8");


            //获取targetid
            String targetIdPattern = "targetId:.*(\\d+),";
            String targetTypePattern = "targetType:.*(\\d+),";
            String userIdPattern = "https://www.d1ev.com/user/(\\d+)";

            String targetId = FetchParamUtil.getMatherStr(body,targetIdPattern);
            String targetType = FetchParamUtil.getMatherStr(body,targetTypePattern);
            String targetUserId = FetchParamUtil.getMatherStr(body,userIdPattern);

            String commonPattern = "(\\d+)";
            targetId = FetchParamUtil.getMatherStr(targetId,commonPattern);
            targetType = FetchParamUtil.getMatherStr(targetType,commonPattern);
            targetUserId = FetchParamUtil.getMatherStr(targetUserId,commonPattern);


            HttpPost httpPost = new HttpPost();
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            /**
             * targetId:77613
             * targetType:1
             * publisherId:0
             * level: 1
             * userId:176252
             * content:赞
             * source:2
             * //wm_csrf_token:3929b0e1a5062e70444addc27e93a0e81
             */
            nameValuePairs.add(new BasicNameValuePair("targetId", targetId));
            nameValuePairs.add(new BasicNameValuePair("targetType", targetType));
            nameValuePairs.add(new BasicNameValuePair("publisherId", "0"));
            nameValuePairs.add(new BasicNameValuePair("level", "1"));
            nameValuePairs.add(new BasicNameValuePair("userId", targetUserId));
            nameValuePairs.add(new BasicNameValuePair("content", requestContext.getContent().getText()));
            nameValuePairs.add(new BasicNameValuePair("source", "2"));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            return httpPost;
        } catch (Exception e) {
            logger.error("[TaiPingYangHandler.createCommentPost]createCommentPost  UrlEncodedFormEntity error! requestContext" + requestContext);
            return null;
        }
    }

}

