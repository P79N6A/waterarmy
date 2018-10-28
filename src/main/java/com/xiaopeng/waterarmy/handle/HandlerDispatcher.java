package com.xiaopeng.waterarmy.handle;

import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.constants.RequestConsts;
import com.xiaopeng.waterarmy.common.enums.PlatformEnum;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.common.enums.TaskEntryTypeEnum;
import com.xiaopeng.waterarmy.common.enums.TaskTypeEnum;
import com.xiaopeng.waterarmy.handle.impl.*;
import com.xiaopeng.waterarmy.handle.param.Content;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;


/**
 * 转发所有请求，找到相应的处理类
 */
@Component
public class HandlerDispatcher {

    @Autowired
    private TaiPingYangHandler taiPingYangHandler;

    @Autowired
    private YiCheHandler yiCheHandler;

    @Autowired
    private AutoHomeHandler autoHomeHandler;

    @Autowired
    private AiKaHandler aiKaHandler;

    @Autowired
    private QiCheTouTiaoHandler qiCheTouTiaoHandler;

    @Autowired
    private DiYiDianDongHandler diYiDianDongHandler;


    public Result<HandlerResultDTO> dispatch(RequestContext requestContext) {

        requestContext = this.createAutoHomeContext();

        switch (requestContext.getPlatform()){
            case PCAUTO:
                 return taiPingYangHandler.handle(requestContext);
            case QCTT:
                return qiCheTouTiaoHandler.handle(requestContext);
            case D1EV:
                return diYiDianDongHandler.handle(requestContext);
            case XCAR:
                return aiKaHandler.handle(requestContext);
            case YICHE:
                return yiCheHandler.handle(requestContext);
            case AUTOHOME:
                return autoHomeHandler.handle(requestContext);
            default:
                return new Result<HandlerResultDTO>(null,false, ResultCodeEnum.HANDLER_NOT_FOUND.getIndex(),ResultCodeEnum.HANDLER_NOT_FOUND.getDesc());
        }
    }

    private RequestContext createAutoHomeContext() {
        RequestContext requestContext = new RequestContext();
        Content content = new Content();
        content.setText("这个车真心不错，我昨天试了一下，好想买");
        content.setTitle("有没有一起组团去买车的");
        requestContext.setContent(content);
        requestContext.setUserId(17L);
        requestContext.setUserLoginId("13216894048");
        requestContext.setHandleType(TaskTypeEnum.POSIED);
        requestContext.setPlatform(PlatformEnum.AUTOHOME);
        requestContext.setPrefixUrl("https://club.autohome.com.cn/bbs/forum-c-3465-1.html");
        return requestContext;
    }

    private RequestContext createTestContext() {
        RequestContext requestContext = new RequestContext();
        Content content = new Content();
        content.setText("这个车真心不错，我昨天试了一下，好想买");
        requestContext.setContent(content);
        requestContext.setUserId(2L);
        requestContext.setUserLoginId("18482193356");
        requestContext.setHandleType(TaskTypeEnum.COMMENT);
        requestContext.setPlatform(PlatformEnum.PCAUTO);
        requestContext.setPrefixUrl("http://baa.bitauto.com/jilidihaogs/thread-15766298-goto179749454.html");
        return requestContext;
    }

    private RequestContext createTestPublisContext() {
        RequestContext requestContext = new RequestContext();
        Content content = new Content();
        content.setText("这个车真的很帅");
        content.setTitle("改装");
        requestContext.setContent(content);
        requestContext.setUserId(4L);
        requestContext.setUserLoginId("15164577148");
        requestContext.setHandleType(TaskTypeEnum.POSIED);
        requestContext.setPlatform(PlatformEnum.YICHE);
        //requestContext.setHandleEntryType(TaskEntryTypeEnum.YICHECOMMENTPRAISE);
        //requestContext.setHandleEntryType(TaskEntryTypeEnum.YICHENEWSCOMMENT);
        requestContext.setPrefixUrl("http://baa.bitauto.com/changancs35/");
        return requestContext;
    }


    private RequestContext createTestYichePublisContext() {
        RequestContext requestContext = new RequestContext();
        Content content = new Content();
        content.setText("这个车真的很帅");
        content.setTitle("改装");
        requestContext.setContent(content);
        requestContext.setUserId(5L);
        requestContext.setUserLoginId("15164577148");
        requestContext.setHandleType(TaskTypeEnum.POSIED);
        requestContext.setPlatform(PlatformEnum.YICHE);
        //requestContext.setHandleEntryType(TaskEntryTypeEnum.YICHECOMMENTPRAISE);
        //requestContext.setHandleEntryType(TaskEntryTypeEnum.YICHENEWSCOMMENT);
        //requestContext.setPrefixUrl("http://www.xcar.com.cn/bbs/forumdisplay.php?fid=1745");
        requestContext.setPrefixUrl("http://baa.bitauto.com/changancs35/");
        //requestContext.setPrefixUrl("http://www.xcar.com.cn/bbs/viewthread.php?tid=34241863");
        //requestContext.setPrefixUrl("http://news.bitauto.com/hao/wenzhang/963762");
       /* HashMap map = new HashMap();
        map.put(RequestConsts.COMMENT_ID,"257971069489512448");
        map.put(RequestConsts.COMMENT_CONTENT,"666");
        requestContext.setRequestParam(map);https://cmt.pcauto.com.cn/action/comment/create.jsp?urlHandle=1*/
        return requestContext;
    }


    private RequestContext createTestTaiPingYangContext() {
        RequestContext requestContext = new RequestContext();
        Content content = new Content();
        content.setText("明年再买吧，有点贵");
        content.setTitle("改装");
        requestContext.setContent(content);
        requestContext.setUserId(14L);
        requestContext.setUserLoginId("18927512986");
        requestContext.setHandleType(TaskTypeEnum.COMMENT);
        requestContext.setPlatform(PlatformEnum.PCAUTO);
        requestContext.setHandleEntryType(TaskEntryTypeEnum.TAIPINGYANGCHEZHUCOMMENT);
        //requestContext.setPrefixUrl("https://www.pcauto.com.cn/nation/1323/13233103.html");
        requestContext.setPrefixUrl("https://price.pcauto.com.cn/comment/sg21775/m79917/view_884063.html");
        HashMap map = new HashMap();
        map.put(RequestConsts.COMMENT_ID,"32202092");
        map.put(RequestConsts.COMMENT_CONTENT,"666");
        requestContext.setRequestParam(map);
        return requestContext;
    }

    private RequestContext createTestTouTiaoContext() {
        RequestContext requestContext = new RequestContext();
        Content content = new Content();
        content.setText("明年再买吧，有点贵");
        content.setTitle("改装");
        requestContext.setContent(content);
        requestContext.setUserId(12L);
        requestContext.setUserLoginId("18927512986");
        requestContext.setHandleType(TaskTypeEnum.COMMENT);
        requestContext.setPlatform(PlatformEnum.QCTT);
        requestContext.setPrefixUrl("https://www.qctt.cn/news/367746");
       /* HashMap map = new HashMap();
        map.put(RequestConsts.COMMENT_ID,"32202092");
        map.put(RequestConsts.COMMENT_CONTENT,"666");
        requestContext.setRequestParam(map);*/
        return requestContext;
    }

    /**
     * 汽车头条视频评论
     * @return
     */
    private RequestContext createTestTouTiaoVideoContext() {
        RequestContext requestContext = new RequestContext();
        Content content = new Content();
        content.setText("明年再买吧，有点贵");
        content.setTitle("改装");
        requestContext.setContent(content);
        requestContext.setUserId(12L);
        requestContext.setUserLoginId("18927512986");
        requestContext.setHandleType(TaskTypeEnum.COMMENT);
        requestContext.setPlatform(PlatformEnum.QCTT);
        requestContext.setHandleEntryType(TaskEntryTypeEnum.QICHEVIDEOCOMMENT);
        requestContext.setPrefixUrl("https://www.qctt.cn/video/105230");
        return requestContext;
    }

    /**
     * 第一电动文章评论
     */
    private RequestContext createTestDiyiDiandongContext() {
        RequestContext requestContext = new RequestContext();
        Content content = new Content();
        content.setText("明年再买吧，有点贵");
        content.setTitle("改装");
        requestContext.setContent(content);
        requestContext.setUserId(9L);
        requestContext.setUserLoginId("18927512986");
        requestContext.setHandleType(TaskTypeEnum.COMMENT);
        requestContext.setPlatform(PlatformEnum.D1EV);
        //requestContext.setHandleEntryType(TaskEntryTypeEnum.QICHEVIDEOCOMMENT);
        requestContext.setPrefixUrl("https://www.d1ev.com/news/qiye/77783");
        return requestContext;
    }

    /**
     * 易车 车家号评论
     */
    private RequestContext createTestYiCheJiaHaoContext() {
        RequestContext requestContext = new RequestContext();
        Content content = new Content();
        content.setText("明年再买吧，有点贵");
        content.setTitle("改装");
        requestContext.setContent(content);
        requestContext.setUserId(13L);
        requestContext.setUserLoginId("18927512986");
        requestContext.setHandleType(TaskTypeEnum.COMMENT);
        requestContext.setPlatform(PlatformEnum.YICHE);
        requestContext.setHandleEntryType(TaskEntryTypeEnum.YICHENEWSCOMMENT);
        requestContext.setPrefixUrl("http://news.bitauto.com/hao/wenzhang/963762");
        return requestContext;
    }


    private RequestContext createTestYiCheKouBeiContext() {
        RequestContext requestContext = new RequestContext();
        Content content = new Content();
        content.setText("明年再买吧，有点贵");
        content.setTitle("改装");
        requestContext.setContent(content);
        requestContext.setUserId(13L);
        requestContext.setUserLoginId("18927512986");
        requestContext.setHandleType(TaskTypeEnum.COMMENT);
        requestContext.setPlatform(PlatformEnum.YICHE);
        requestContext.setHandleEntryType(TaskEntryTypeEnum.YICHEKOUBEICOMMENT);
        requestContext.setPrefixUrl("http://car.bitauto.com/dibadaiyage/koubei/968281/");
        return requestContext;
    }
}
