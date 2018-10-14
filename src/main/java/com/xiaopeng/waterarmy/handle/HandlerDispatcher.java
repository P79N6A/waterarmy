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
    private AiKaHandler aiKaHandler;

    @Autowired
    private QiCheTouTiaoHandler qiCheTouTiaoHandler;

    @Autowired
    private DiYiDianDongHandler diYiDianDongHandler;


    public Result<HandlerResultDTO> dispatch(RequestContext requestContext) {

        requestContext = this.createTestTaiPingYangContext();

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
            default:
                return new Result<HandlerResultDTO>(null,false, ResultCodeEnum.HANDLER_NOT_FOUND.getIndex(),ResultCodeEnum.HANDLER_NOT_FOUND.getDesc());
        }
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
        content.setText("有没有改装的，外表加内饰？");
        content.setTitle("改装");
        requestContext.setContent(content);
        requestContext.setUserId(4L);
        requestContext.setUserLoginId("18383849422");
        requestContext.setHandleType(TaskTypeEnum.COMMENT);
        requestContext.setPlatform(PlatformEnum.YICHE);
        //requestContext.setHandleEntryType(TaskEntryTypeEnum.YICHECOMMENTPRAISE);
        requestContext.setHandleEntryType(TaskEntryTypeEnum.YICHEKOUBEICOMMENT);
        //requestContext.setPrefixUrl("http://www.xcar.com.cn/bbs/forumdisplay.php?fid=1745");
       // requestContext.setPrefixUrl("http://news.bitauto.com/qichewenhua/20181008/1008346394.html");
        //requestContext.setPrefixUrl("http://www.xcar.com.cn/bbs/viewthread.php?tid=34241863");
        requestContext.setPrefixUrl("http://car.bitauto.com/dibadaiyage/koubei/968281/");
        HashMap map = new HashMap();
        map.put(RequestConsts.COMMENT_ID,"257971069489512448");
        map.put(RequestConsts.COMMENT_CONTENT,"666");
        requestContext.setRequestParam(map);https://cmt.pcauto.com.cn/action/comment/create.jsp?urlHandle=1
        return requestContext;
    }



    private RequestContext createTestTaiPingYangContext() {
        RequestContext requestContext = new RequestContext();
        Content content = new Content();
        content.setText("明年再买吧，有点贵");
        content.setTitle("改装");
        requestContext.setContent(content);
        requestContext.setUserId(7L);
        requestContext.setUserLoginId("18482193356");
        requestContext.setHandleType(TaskTypeEnum.COMMENT);
        requestContext.setPlatform(PlatformEnum.PCAUTO);
        requestContext.setHandleEntryType(TaskEntryTypeEnum.TAIPINGYANGCHEZHUCOMMENT);
        //requestContext.setPrefixUrl("https://www.pcauto.com.cn/nation/1323/13233103.html");
        requestContext.setPrefixUrl("https://price.pcauto.com.cn/comment/sg12072/m37198/view_878074.html");
        HashMap map = new HashMap();
        map.put(RequestConsts.COMMENT_ID,"32202092");
        map.put(RequestConsts.COMMENT_CONTENT,"666");
        requestContext.setRequestParam(map);
        return requestContext;
    }


}
