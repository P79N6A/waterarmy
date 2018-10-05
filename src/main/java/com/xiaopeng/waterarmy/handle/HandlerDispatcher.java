package com.xiaopeng.waterarmy.handle;

import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.enums.PlatformEnum;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.common.enums.TaskTypeEnum;
import com.xiaopeng.waterarmy.handle.impl.*;
import com.xiaopeng.waterarmy.handle.param.Content;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


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

        requestContext = this.createTestPublisContext();

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
        requestContext.setPrefixUrl("https://bbs.pcauto.com.cn/topic-17493073.html");
        return requestContext;
    }

    private RequestContext createTestPublisContext() {
        RequestContext requestContext = new RequestContext();
        Content content = new Content();
        content.setText("出来工作也有些年头了，一个人在外面的确不容易，每天上班搭公交，每次回家坐长途，真的很累，驾照考出来都两年多了");
        content.setTitle("这车什么时候量产上市");
        requestContext.setContent(content);
        requestContext.setUserId(4L);
        requestContext.setUserLoginId("18383849422");
        requestContext.setHandleType(TaskTypeEnum.POSIED);
        requestContext.setPlatform(PlatformEnum.YICHE);
        requestContext.setPrefixUrl("http://baa.bitauto.com/langdong/");
        return requestContext;
    }


}
