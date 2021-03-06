package com.xiaopeng.waterarmy.handle.Util;

import com.xiaopeng.waterarmy.common.constants.RequestConsts;
import com.xiaopeng.waterarmy.common.constants.ResultConstants;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import com.xiaopeng.waterarmy.model.dao.CommentInfo;
import com.xiaopeng.waterarmy.model.dao.PraiseInfo;
import com.xiaopeng.waterarmy.model.dao.PublishInfo;

public class ResultParamUtil {
    public static CommentInfo createCommentInfo(RequestContext requestContext, String content) {
        CommentInfo commentInfo = new CommentInfo();
        commentInfo.setOutUserName(requestContext.getUserLoginId());
        commentInfo.setDetailResult(content);
        commentInfo.setPlatform(requestContext.getPlatform().getName());
        commentInfo.setStatus(ResultConstants.STATUS_ENABLE);
        commentInfo.setUserId(requestContext.getUserId());
        commentInfo.setTargetUrl(requestContext.getPrefixUrl());
        return commentInfo;
    }

    public static PraiseInfo createPraiseInfo(RequestContext requestContext, String content) {
        PraiseInfo praiseInfo = new PraiseInfo();
        praiseInfo.setOutUserName(requestContext.getUserLoginId());
        praiseInfo.setDetailResult(content);
        praiseInfo.setPlatform(requestContext.getPlatform().getName());
        praiseInfo.setStatus(ResultConstants.STATUS_ENABLE);
        praiseInfo.setUserId(requestContext.getUserId());
        praiseInfo.setTargetUrl(requestContext.getPrefixUrl());
        praiseInfo.setTargetUrl(requestContext.getPrefixUrl());
        praiseInfo.setCommentId((String) requestContext.getRequestParam().get(RequestConsts.COMMENT_ID));
        if (requestContext.getRequestParam().get(RequestConsts.COMMENT_CONTENT)!=null) {
            praiseInfo.setCommentContent((String)requestContext.getRequestParam().get(RequestConsts.COMMENT_CONTENT));
        }
        return praiseInfo;
    }

    public static PublishInfo createPublishInfo(RequestContext requestContext, String content, String targetUrl) {
        PublishInfo publishInfo = new PublishInfo();
        publishInfo.setOutUserName(requestContext.getUserLoginId());
        publishInfo.setDetailResult(content);
        publishInfo.setPlatform(requestContext.getPlatform().getName());
        publishInfo.setStatus(ResultConstants.STATUS_ENABLE);
        publishInfo.setUserId(requestContext.getUserId());
        publishInfo.setTargetUrl(requestContext.getTargetUrl());
        publishInfo.setTitle(requestContext.getContent().getTitle());
        publishInfo.setBody(requestContext.getContent().getText());
        publishInfo.setTargetUrl(targetUrl);
        return publishInfo;
    }

    public static HandlerResultDTO createHandlerResultDTO(RequestContext requestContext, String content) {
        HandlerResultDTO handlerResultDTO = new HandlerResultDTO();
        handlerResultDTO.setHandleType(requestContext.getHandleType());
        handlerResultDTO.setDetailResult(content);
        handlerResultDTO.setPlatform(requestContext.getPlatform());
        handlerResultDTO.setUserId(requestContext.getUserId());
        handlerResultDTO.setUserLoginId(requestContext.getUserLoginId());
        handlerResultDTO.setTargetUrl(requestContext.getTargetUrl());
        return handlerResultDTO;
    }

    public static HandlerResultDTO createHandlerResultDTO(RequestContext requestContext, String content, String targetUrl) {
        HandlerResultDTO handlerResultDTO = new HandlerResultDTO();
        handlerResultDTO.setHandleType(requestContext.getHandleType());
        handlerResultDTO.setDetailResult(content);
        handlerResultDTO.setPlatform(requestContext.getPlatform());
        handlerResultDTO.setUserId(requestContext.getUserId());
        handlerResultDTO.setUserLoginId(requestContext.getUserLoginId());
        handlerResultDTO.setTargetUrl(targetUrl);
        return handlerResultDTO;
    }
}
