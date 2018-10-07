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

        //requestContext = this.createTestPublisContext();

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
        content.setTitle("想改装，但是不知道怎么改，改成什么样子。一直在纠结，还要不要改。家里4口人都不同意改装，主要还是资金问题！");
        requestContext.setContent(content);
        requestContext.setUserId(2L);
        requestContext.setUserLoginId("13438042646");
        requestContext.setHandleType(TaskTypeEnum.POSIED);
        requestContext.setPlatform(PlatformEnum.XCAR);
        requestContext.setPrefixUrl("http://www.xcar.com.cn/bbs/forumdisplay.php?fid=1745");
        return requestContext;
    }


}
