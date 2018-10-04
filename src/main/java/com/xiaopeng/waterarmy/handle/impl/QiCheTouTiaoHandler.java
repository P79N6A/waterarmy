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

import java.util.ArrayList;
import java.util.List;

public class QiCheTouTiaoHandler extends PlatformHandler {

    private static Logger logger = LoggerFactory.getLogger(QiCheTouTiaoLoginHandler.class);

    @Autowired
    private QiCheTouTiaoLoginHandler qiCheTouTiaoLoginHandler;

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
        Result<LoginResultDTO> resultDTOResult = qiCheTouTiaoLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[QiCheTouTiaoHandler.comment] requestContext" + requestContext);
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
        //汽车头条评论参数
        /**
         * comment:真心不错
         * new_id:367435
         * _token:oGQAewctAFVzqvAFILnCOF6QxZJv0K02qlpP5aR7
         */
        HttpPost httpPost = new HttpPost();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("comment", requestContext.getContent().getText()));
        nameValuePairs.add(new BasicNameValuePair("new_id", getNewId(requestContext.getTargetUrl())));
        nameValuePairs.add(new BasicNameValuePair("_token", loginResultDTO.getToken()));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (Exception e) {
            logger.error("[TaiPingYangHandler.createCommentPost]createCommentPost  UrlEncodedFormEntity error! nameValuePairs" + nameValuePairs);
            return null;
        }
        return httpPost;
    }

    private String getNewId(String  url) {
            String pattern = "(\\d+)";
            return FetchParamUtil.getMatherStr(url,pattern);
    }
}
